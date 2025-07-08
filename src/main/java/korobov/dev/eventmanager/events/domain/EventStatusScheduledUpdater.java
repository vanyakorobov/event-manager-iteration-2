package korobov.dev.eventmanager.events.domain;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import korobov.dev.eventmanager.events.db.EventRepository;

import java.util.List;

@EnableScheduling
@Configuration
public class EventStatusScheduledUpdater {

    private final static Logger log = LoggerFactory.getLogger(EventStatusScheduledUpdater.class);

    private final EventRepository eventRepository;

    public EventStatusScheduledUpdater(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "${event.stats.cron}")
    @Transactional
    public void updateEventStatuses() {
        log.info("EventStatusScheduledUpdater started");

        List<Long> toStart = eventRepository.findStartedEventsWithStatus(EventStatus.WAIT_START);

        if (!toStart.isEmpty()) {
            int startedCount = eventRepository.updateStatusByIds(toStart, EventStatus.STARTED);
            log.info("Updated {} events to status STARTED", startedCount);
        } else {
            log.info("No events to start");
        }


        List<Long> toFinish = eventRepository.findEndedEventsWithStatus(EventStatus.STARTED);
        if (!toFinish.isEmpty()) {
            int finishedCount = eventRepository.updateStatusByIds(toFinish, EventStatus.FINISHED);
            log.info("Updated {} events to status FINISHED", finishedCount);
        } else {
            log.info("No events to finish");
        }
    }

}


