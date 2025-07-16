package korobov.dev.eventnotificator.service;

import korobov.dev.eventnotificator.dto.EventChangeKafkaMessage;
import korobov.dev.eventnotificator.entity.Notification;
import korobov.dev.eventnotificator.repository.NotificationRepository;
import korobov.dev.eventnotificator.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final String topic;

    public NotificationConsumer(
            NotificationRepository repository,
            @Value("${app.kafka.topic}") String topic
    ) {
        this.repository = repository;
        this.topic = topic;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(EventChangeKafkaMessage msg) {
        // Для каждого подписчика сохраняем нотификацию
        msg.getUsers().forEach(userId -> {
            Notification notification = new Notification(
                    userId,
                    msg.getEventId(),
                    // сериализуем DTO в JSON строку или используем ObjectMapper
                    JsonUtil.toJson(msg)
            );
            repository.save(notification);
        });
    }
}
