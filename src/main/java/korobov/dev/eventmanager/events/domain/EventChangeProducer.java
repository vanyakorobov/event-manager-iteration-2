package main.java.korobov.dev.eventmanager.events.domain;

import korobov.dev.eventmanager.events.dto.EventChangeKafkaMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventChangeProducer {

    private final KafkaTemplate<String, EventChangeKafkaMessage> template;
    private final String topic;

    public EventChangeProducer(
            KafkaTemplate<String, EventChangeKafkaMessage> template,
            @Value("${app.kafka.topic}") String topic
    ) {
        this.template = template;
        this.topic = topic;
    }

    public void send(EventChangeKafkaMessage msg) {

        template.send(topic, msg.getEventId().toString(), msg);
    }
}

