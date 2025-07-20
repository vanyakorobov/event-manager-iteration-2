package korobov.dev.eventmanager.events.db;

import org.springframework.stereotype.Component;
import korobov.dev.eventmanager.events.domain.Event;
import korobov.dev.eventmanager.events.domain.EventRegistration;

@Component
public class EventEntityMapper {

    public Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getName(),
                entity.getOwnerId(),
                entity.getMaxPlaces(),
                entity.getRegistrationList().stream()
                        .map(it -> new EventRegistration(
                                it.getId(),
                                it.getUserId(),
                                entity.getId())
                        )
                        .toList(),
                entity.getDate(),
                entity.getCost(),
                entity.getDuration(),
                entity.getLocationId(),
                entity.getStatus()
        );
    }

}
