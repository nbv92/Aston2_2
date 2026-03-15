package org.example.notification.api;

import org.example.notification.MailNotificationService;
import org.example.notification.api.dto.SendMailEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MailSendConsumer {

    private final MailNotificationService mailService;

    public MailSendConsumer(MailNotificationService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.send-mail}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(SendMailEvent event) {
        mailService.send(event.getTo(), event.getSubject(), event.getText());
    }
}