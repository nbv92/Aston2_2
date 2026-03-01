import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.example.events.UserLifecycleEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class NotificationKafkaIT {

    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.7.0");

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        r.add("spring.mail.host", () -> "localhost");
        r.add("spring.mail.port", () -> greenMail.getSmtp().getPort());

        r.add("app.kafka.topics.user-lifecycle", () -> "user.lifecycle");
        r.add("app.mail.from", () -> "no-reply@your-site.com");
    }

    @Autowired
    KafkaTemplate<String, UserLifecycleEvent> kafkaTemplate;

    @Test
    void createdEvent_sendsEmail() {
        greenMail.purgeEmailFromAllMailboxes();

        String email = "user1@test.com";
        kafkaTemplate.send("user.lifecycle", email,
                new UserLifecycleEvent(UserLifecycleEvent.Operation.CREATED, email));

        await().atMost(10, SECONDS).untilAsserted(() -> {
            MimeMessage[] messages = greenMail.getReceivedMessages();
            assertEquals(1, messages.length);
            assertEquals("Уведомление", messages[0].getSubject());
            assertTrue(messages[0].getContent().toString().contains("успешно создан"));
        });
    }

    @Test
    void deletedEvent_sendsEmail() {
        greenMail.purgeEmailFromAllMailboxes();
        String email = "user2@test.com";
        kafkaTemplate.send("user.lifecycle", email,
                new UserLifecycleEvent(UserLifecycleEvent.Operation.DELETED, email));

        await().atMost(10, SECONDS).untilAsserted(() -> {
            MimeMessage[] messages = greenMail.getReceivedMessages();
            assertEquals(1, messages.length);
            assertTrue(messages[0].getContent().toString().contains("Ваш аккаунт был удалён"));
        });
    }
}
