package org.example.kafka;

import org.example.event.UserLifecycleEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserLifecycleEventPublisher {

    private final KafkaTemplate<String, UserLifecycleEvent> kafkaTemplate;
    private final String topic;

    public UserLifecycleEventPublisher(
            KafkaTemplate<String, UserLifecycleEvent> kafkaTemplate,
            @Value("${app.kafka.topics.user-lifecycle}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(UserLifecycleEvent event) {
        kafkaTemplate.send(topic, event.email(), event);
    }
}