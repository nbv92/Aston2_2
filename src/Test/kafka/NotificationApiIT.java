import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationApiIT {

    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.7.0");

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        r.add("spring.mail.host", () -> "localhost");
        r.add("spring.mail.port", () -> greenMail.getSmtp().getPort());

        r.add("app.mail.from", () -> "no-reply@your-site.com");
    }

    @Autowired
    TestRestTemplate rest;

    @Test
    void apiSendEmail_sendsEmail() {
        greenMail.purgeEmailFromAllMailboxes();

        String body = """
                {"email":"api@test.com","operation":"CREATED"}
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> resp = rest.exchange(
                "/api/notifications/email",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Void.class
        );

        assertEquals(HttpStatus.ACCEPTED, resp.getStatusCode());

        await().atMost(10, SECONDS).untilAsserted(() -> {
            MimeMessage[] messages = greenMail.getReceivedMessages();
            assertEquals(1, messages.length);
            assertTrue(messages[0].getContent().toString().contains("успешно создан"));
        });
    }
}