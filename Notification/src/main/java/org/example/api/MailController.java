package org.example.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.notification.api.dto.SendMailRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Mail", description = "Отправка email-уведомлений")
@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailNotificationService mail;

    public MailController(MailNotificationService mail) {
        this.mail = mail;
    }

    @Operation(summary = "Отправить email (синхронно через HTTP)")
    @PostMapping("/send")
    public ResponseEntity<EntityModel<Void>> send(@Valid @RequestBody SendMailRequest request) {
        mail.send(request.getTo(), request.getSubject(), request.getText());

        EntityModel<Void> model = EntityModel.of(null);
        model.add(linkTo(methodOn(MailController.class).send(request)).withSelfRel());
        model.add(linkTo(MailController.class).withRel("mail"));
        return ResponseEntity.accepted().body(model);
    }
}