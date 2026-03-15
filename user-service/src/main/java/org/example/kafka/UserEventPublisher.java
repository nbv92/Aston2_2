package org.example.kafka;

import org.example.event.UserEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final String topic;

    public UserEventPublisher(
            KafkaTemplate<String, UserEvent> kafkaTemplate,
            @Value("${app.kafka.topics.user-lifecycle}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(UserEvent event) {
        kafkaTemplate.send(topic, event.email(), event);
    }
}
