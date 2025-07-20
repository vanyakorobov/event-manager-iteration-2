package korobov.dev.eventmanager.events.domain;

import jakarta.transaction.Transactional;
import korobov.dev.eventmanager.events.db.EventRepository;
import korobov.dev.eventmanager.events.dto.EventChangeKafkaMessage;
import korobov.dev.eventmanager.events.dto.FieldChange;
import korobov.dev.eventmanager.events.service.EventChangeProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@EnableScheduling
@Configuration
public class EventStatusScheduledUpdater {

    private static final Logger log = LoggerFactory.getLogger(EventStatusScheduledUpdater.class);

    private final EventRepository eventRepository;
    private final EventChangeProducer eventChangeProducer;

    public EventStatusScheduledUpdater(EventRepository eventRepository,
                                       EventChangeProducer eventChangeProducer) {
        this.eventRepository = eventRepository;
        this.eventChangeProducer = eventChangeProducer;
    }

    @Scheduled(cron = "${event.stats.cron}")
    @Transactional
    public void updateEventStatuses() {
        log.info("EventStatusScheduledUpdater started");

        // 1) Переводим WAIT_START → STARTED
        List<Long> toStart = eventRepository.findStartedEventsWithStatus(EventStatus.WAIT_START);
        if (!toStart.isEmpty()) {
            int startedCount = eventRepository.updateStatusByIds(toStart, EventStatus.STARTED);
            log.info("Updated {} events to status STARTED", startedCount);

            // Отправляем в Kafka для каждого события
            toStart.forEach(eventId -> {
                EventChangeKafkaMessage msg = new EventChangeKafkaMessage();
                msg.setEventId(eventId);
                msg.setUsers(eventRepository.findSubscriberIdsByEventId(eventId));
                msg.setStatus(new FieldChange<>(EventStatus.WAIT_START, EventStatus.STARTED));
                eventChangeProducer.send(msg);
            });
        } else {
            log.info("No events to start");
        }

        // 2) Переводим STARTED → FINISHED
        List<Long> toFinish = eventRepository.findEndedEventsWithStatus(EventStatus.STARTED);
        if (!toFinish.isEmpty()) {
            int finishedCount = eventRepository.updateStatusByIds(toFinish, EventStatus.FINISHED);
            log.info("Updated {} events to status FINISHED", finishedCount);

            // Отправляем в Kafka для каждого события
            toFinish.forEach(eventId -> {
                EventChangeKafkaMessage msg = new EventChangeKafkaMessage();
                msg.setEventId(eventId);
                msg.setUsers(eventRepository.findSubscriberIdsByEventId(eventId));
                msg.setStatus(new FieldChange<>(EventStatus.STARTED, EventStatus.FINISHED));
                eventChangeProducer.send(msg);
            });
        } else {
            log.info("No events to finish");
        }
    }
}
