package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.SendEmailRequest;
import org.example.notification.MailNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final MailNotificationService mail;

    public NotificationController(MailNotificationService mail) {
        this.mail = mail;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> send(@RequestBody @Valid SendEmailRequest req) {
        String op = req.operation().trim().toUpperCase();
        String subject = "Уведомление";

        String text = switch (op) {
            case "DELETED" -> "Здравствуйте! Ваш аккаунт был удалён.";
            case "CREATED" -> "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
            default -> throw new IllegalArgumentException("Unsupported operation: " + req.operation());
        };

        mail.send(req.email(), subject, text);
        return ResponseEntity.accepted().build();
    }
}
