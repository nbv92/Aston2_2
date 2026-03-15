package org.example.notification.api;

import org.example.notification.MailNotificationService;
import org.example.notification.api.dto.SendMailEvent;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class MailSendConsumerTest {

    @Test
    void onMessage_shouldCallMailService() {
        MailNotificationService svc = mock(MailNotificationService.class);
        MailSendConsumer consumer = new MailSendConsumer(svc);

        consumer.onMessage(new SendMailEvent("a@b.com", "s", "t"));

        verify(svc).send("a@b.com", "s", "t");
    }
}