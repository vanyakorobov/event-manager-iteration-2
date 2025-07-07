package korobov.dev.eventmanager.events.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import korobov.dev.eventmanager.events.db.EventRepository;

@EnableScheduling
@Configuration
public class EventStatusScheduledUpdater {

    private final static Logger log = LoggerFactory.getLogger(EventStatusScheduledUpdater.class);

    private final EventRepository eventRepository;

    public EventStatusScheduledUpdater(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${event.stats.cron}")
    public void updateEventStatuses() {
        log.info("EventStatusScheduledUpdater started");

        var startedEvents = eventRepository.findStartedEventsWithStatus(EventStatus.WAIT_START);
        startedEvents.forEach(eventId ->
                eventRepository.changeEventStatus(eventId, EventStatus.STARTED)
        );

        var endedEvents = eventRepository.findEndedEventsWithStatus(EventStatus.STARTED);
        endedEvents.forEach(eventId ->
                eventRepository.changeEventStatus(eventId, EventStatus.FINISHED)
        );
    }

}


