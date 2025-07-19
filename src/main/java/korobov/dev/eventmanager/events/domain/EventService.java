package korobov.dev.eventmanager.events.domain;

import jakarta.persistence.EntityNotFoundException;
import korobov.dev.eventmanager.events.api.EventCreateRequestDto;
import korobov.dev.eventmanager.events.api.EventSearchFilter;
import korobov.dev.eventmanager.events.api.EventUpdateRequestDto;
import korobov.dev.eventmanager.events.db.EventEntity;
import korobov.dev.eventmanager.events.db.EventEntityMapper;
import korobov.dev.eventmanager.events.db.EventRepository;
import korobov.dev.eventmanager.locations.LocationService;
import korobov.dev.eventmanager.users.domain.AuthenticationService;
import korobov.dev.eventmanager.users.domain.UserRole;
import korobov.dev.eventmanager.service.EventChangeProducer;
import korobov.dev.eventmanager.dto.EventChangeKafkaMessage;
import korobov.dev.eventmanager.dto.FieldChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final AuthenticationService authenticationService;
    private final EventEntityMapper entityMapper;
    private final EventChangeProducer eventChangeProducer; // ← продюсер Kafka

    public EventService(
            EventRepository eventRepository,
            LocationService locationService,
            AuthenticationService authenticationService,
            EventEntityMapper entityMapper,
            EventChangeProducer eventChangeProducer       // ← внедряем
    ) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.authenticationService = authenticationService;
        this.entityMapper = entityMapper;
        this.eventChangeProducer = eventChangeProducer;
    }

    @Transactional
    public Event createEvent(EventCreateRequestDto createRequest) {
        var location = locationService.getLocationById(createRequest.locationId());
        if (location.capacity() < createRequest.maxPlaces()) {
            throw new IllegalArgumentException(
                    "Capacity of location is: %s, but maxPlaces is: %s"
                            .formatted(location.capacity(), createRequest.maxPlaces()));
        }

        var currentUser = authenticationService.getCurrentAuthenticatedUser();

        var entity = new EventEntity(
                null,
                createRequest.name(),
                currentUser.id(),
                createRequest.maxPlaces(),
                List.of(),
                createRequest.date(),
                createRequest.cost(),
                createRequest.duration(),
                createRequest.locationId(),
                EventStatus.WAIT_START
        );

        entity = eventRepository.save(entity);
        log.info("New event was created: eventId={}", entity.getId());
        return entityMapper.toDomain(entity);
    }

    public Event getEventById(Long eventId) {
        var entity = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Event not found, id=" + eventId));
        return entityMapper.toDomain(entity);
    }

    public void cancelEvent(Long eventId) {
        checkCurrentUserCanModifyEvent(eventId);
        var event = getEventById(eventId);

        if (event.status().equals(EventStatus.CANCELLED)) {
            log.info("Event was already cancelled");
            return;
        }
        if (event.status().equals(EventStatus.STARTED)
                || event.status().equals(EventStatus.FINISHED)) {
            throw new IllegalArgumentException(
                    "Cannot cancel event with status: " + event.status());
        }

        eventRepository.changeEventStatus(eventId, EventStatus.CANCELLED);
    }

    @Transactional
    public Event updateEvent(Long eventId,
                             EventUpdateRequestDto updateRequest) {
        checkCurrentUserCanModifyEvent(eventId);

        // загрузка сущности
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Event not found, id=" + eventId));

        if (!EventStatus.WAIT_START.equals(event.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot modify event in status: " + event.getStatus());
        }

        // валидация location/maxPlaces
        if (updateRequest.locationId() == null) {
            throw new IllegalArgumentException("Cannot update event without location");
        }
        if (updateRequest.maxPlaces() == null) {
            throw new IllegalArgumentException("Cannot update event without maxPlaces");
        }
        var location = locationService.getLocationById(updateRequest.locationId());
        if (location.capacity() < updateRequest.maxPlaces()) {
            throw new IllegalArgumentException(
                    "Capacity of location less than maxPlaces: capacity="
                            + location.capacity() + ", maxPlaces=" + updateRequest.maxPlaces());
        }
        if (event.getRegistrationList().size() > updateRequest.maxPlaces()) {
            throw new IllegalArgumentException(
                    "Registration count is more than maxPlaces: regCount="
                            + event.getRegistrationList().size()
                            + ", maxPlaces=" + updateRequest.maxPlaces());
        }

        // Сохраняем старые значения для Kafka-сообщения
        String oldName        = event.getName();
        Integer oldMaxPlaces  = event.getMaxPlaces();
        LocalDateTime oldDate = event.getDate();
        BigDecimal oldCost    = event.getCost();
        Integer oldDuration   = event.getDuration();
        Long oldLocationId    = event.getLocationId();

        // Применяем новые значения
        event.setName(updateRequest.name());
        event.setMaxPlaces(updateRequest.maxPlaces());
        event.setDate(updateRequest.date());
        event.setCost(updateRequest.cost());
        event.setDuration(updateRequest.duration());
        event.setLocationId(updateRequest.locationId());

        // Сохраняем в БД
        EventEntity saved = eventRepository.save(event);
        Event result = entityMapper.toDomain(saved);

        // Формируем и отправляем Kafka-сообщение
        EventChangeKafkaMessage msg = new EventChangeKafkaMessage();
        msg.setEventId(saved.getId());
        msg.setOwnerId(saved.getOwnerId());
        msg.setChangedById(authenticationService.getCurrentAuthenticatedUser().id());
        msg.setUsers(
                eventRepository.findSubscriberIdsByEventId(saved.getId())
        );

        if (!oldName.equals(updateRequest.name())) {
            msg.setName(new FieldChange<>(oldName, updateRequest.name()));
        }
        if (!oldMaxPlaces.equals(updateRequest.maxPlaces())) {
            msg.setMaxPlaces(new FieldChange<>(oldMaxPlaces, updateRequest.maxPlaces()));
        }
        if (!oldDate.equals(updateRequest.date())) {
            msg.setDate(new FieldChange<>(oldDate, updateRequest.date()));
        }
        if (oldCost.compareTo(updateRequest.cost()) != 0) {
            msg.setCost(new FieldChange<>(oldCost, updateRequest.cost()));
        }
        if (!oldDuration.equals(updateRequest.duration())) {
            msg.setDuration(new FieldChange<>(oldDuration, updateRequest.duration()));
        }
        if (!oldLocationId.equals(updateRequest.locationId())) {
            msg.setLocationId(new FieldChange<>(oldLocationId, updateRequest.locationId()));
        }

        eventChangeProducer.send(msg);

        return result;
    }

    private void checkCurrentUserCanModifyEvent(Long eventId) {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        var event = getEventById(eventId);
        if (!event.ownerId().equals(currentUser.id())
                && !currentUser.role().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("This user cannot modify this event");
        }
    }

    public List<Event> searchByFilter(EventSearchFilter searchFilter) {
        var entities = eventRepository.findEvents(
                searchFilter.name(),
                searchFilter.placesMin(),
                searchFilter.placesMax(),
                searchFilter.dateStartAfter(),
                searchFilter.dateStartBefore(),
                searchFilter.costMin(),
                searchFilter.costMax(),
                searchFilter.durationMin(),
                searchFilter.durationMax(),
                searchFilter.locationId(),
                searchFilter.eventStatus()
        );
        return entities.stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public List<Event> getCurrentUserEvents() {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        return eventRepository.findAllByOwnerIdIs(currentUser.id())
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
