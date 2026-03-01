package org.example.kafka;

import org.example.event.UserLifecycleEvent;
import org.example.notification.MailNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class UserLifecycleListener {

    private final MailNotificationService mail;

    public UserLifecycleListener(MailNotificationService mail) {
        this.mail = mail;
    }

    @KafkaListener(topics = "${app.kafka.topics.user-lifecycle}")
    public void onEvent(UserLifecycleEvent event) {
        String subject = "Уведомление";

        String text = switch (event.operation()) {
            case DELETED -> "Здравствуйте! Ваш аккаунт был удалён.";
            case CREATED -> "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        };

        mail.send(event.email(), subject, text);
    }
}