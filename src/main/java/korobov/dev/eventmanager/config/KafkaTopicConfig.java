package main.java.korobov.dev.eventmanager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic eventChangesTopic() {
        return new NewTopic("event-changes", 1, (short) 1);
    }
}
