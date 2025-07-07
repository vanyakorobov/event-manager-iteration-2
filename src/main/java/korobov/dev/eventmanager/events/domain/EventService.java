package korobov.dev.eventmanager.events.domain;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import korobov.dev.eventmanager.events.api.EventCreateRequestDto;
import korobov.dev.eventmanager.events.api.EventSearchFilter;
import korobov.dev.eventmanager.events.api.EventUpdateRequestDto;
import korobov.dev.eventmanager.events.db.EventEntity;
import korobov.dev.eventmanager.events.db.EventEntityMapper;
import korobov.dev.eventmanager.events.db.EventRepository;
import korobov.dev.eventmanager.locations.LocationService;
import korobov.dev.eventmanager.users.domain.AuthenticationService;
import korobov.dev.eventmanager.users.domain.UserRole;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final static Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final AuthenticationService authenticationService;
    private final EventEntityMapper entityMapper;

    public EventService(EventRepository eventRepository,
                        LocationService locationService,
                        AuthenticationService authenticationService,
                        EventEntityMapper entityMapper) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.authenticationService = authenticationService;
        this.entityMapper = entityMapper;
    }

    public Event createEvent(EventCreateRequestDto createRequest) {

        var location = locationService.getLocationById(createRequest.locationId());
        if (location.capacity() < createRequest.maxPlaces()) {
            throw new IllegalArgumentException("Capacity of location is: %s, but maxPlaces is: %s"
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
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event entity wasn't found by id=%s"
                        .formatted(eventId)));

        return entityMapper.toDomain(event);
    }


    public void cancelEvent(Long eventId) {
        checkCurrentUserCanModifyEvent(eventId);
        var event = getEventById(eventId);

        if (event.status().equals(EventStatus.CANCELLED)) {
            log.info("Event was already cancelled");
            return;
        }
        if (event.status().equals(EventStatus.FINISHED)
                || event.status().equals(EventStatus.STARTED)) {
            throw new IllegalArgumentException("Cannot cancel event with status: status=%s"
                    .formatted(event.status()));
        }

        eventRepository.changeEventStatus(eventId, EventStatus.CANCELLED);
    }

    public Event updateEvent(Long eventId,
                             EventUpdateRequestDto updateRequest) {
        checkCurrentUserCanModifyEvent(eventId);
        var event = eventRepository.findById(eventId).orElseThrow();

        if (!event.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Cannot modify event in status: %s"
                    .formatted(event.getStatus()));
        }

        if (updateRequest.maxPlaces() != null || updateRequest.locationId() != null) {
            var locationId = Optional.ofNullable(updateRequest.locationId())
                    .orElse(event.getLocationId());
            var maxPlaces = Optional.ofNullable(updateRequest.maxPlaces())
                    .orElse(event.getMaxPlaces());

            var location = locationService.getLocationById(locationId);
            if (location.capacity() < maxPlaces) {
                throw new IllegalArgumentException(
                        "Capacity of location less than maxPlaces: capacity=%s, maxPlaces=%s"
                                .formatted(location.capacity(), maxPlaces)
                );
            }
        }

        if (updateRequest.maxPlaces() != null
            && event.getRegistrationList().size() > updateRequest.maxPlaces()) {
            throw new IllegalArgumentException(
                    "Registration count is more than maxPlaces: regCount=%s, maxPlaces=%s"
                    .formatted(event.getRegistrationList().size(), updateRequest.maxPlaces()));
        }

        Optional.ofNullable(updateRequest.name())
                .ifPresent(event::setName);
        Optional.ofNullable(updateRequest.maxPlaces())
                .ifPresent(event::setMaxPlaces);
        Optional.ofNullable(updateRequest.date())
                .ifPresent(event::setDate);
        Optional.ofNullable(updateRequest.cost())
                .ifPresent(event::setCost);
        Optional.ofNullable(updateRequest.duration())
                .ifPresent(event::setDuration);
        Optional.ofNullable(updateRequest.locationId())
                .ifPresent(event::setLocationId);

        eventRepository.save(event);

        return entityMapper.toDomain(event);
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
        var foundEntities =  eventRepository.findEvents(
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

        return foundEntities.stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public List<Event> getCurrentUserEvents() {
        var currentUser = authenticationService.getCurrentAuthenticatedUser();
        var userEvents = eventRepository.findAllByOwnerIdIs(currentUser.id());

        return userEvents.stream()
                .map(entityMapper::toDomain)
                .toList();
    }
}
