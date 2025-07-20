package main.java.korobov.dev.eventmanager.service;

import korobov.dev.eventmanager.dto.EventChangeKafkaMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventChangeProducer {

    private final KafkaTemplate<String, EventChangeKafkaMessage> kafka;
    private final String topic;

    public EventChangeProducer(KafkaTemplate<String, EventChangeKafkaMessage> kafka,
                               @Value("${app.kafka.topic}") String topic) {
        this.kafka = kafka;
        this.topic = topic;
    }

    public void send(EventChangeKafkaMessage msg) {
        kafka.send(topic, msg.getEventId().toString(), msg);
    }
}