package org.example.notification;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
public class MailController {
    private final MailNotificationService mail;

    public MailController(MailNotificationService mail) {
        this.mail = mail;
    }

    @PostMapping("/send")
    public void send(@RequestParam String to,
                     @RequestParam String subject,
                     @RequestParam String text) {
        mail.send(to, subject, text);
    }
}