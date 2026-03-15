package org.example.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.notification.api.dto.SendMailRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MailController.class)
class MailControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean MailNotificationService mailNotificationService;

    @Test
    void send_shouldReturn202_andHateoasLinks() throws Exception {
        SendMailRequest req = new SendMailRequest();
        req.setTo("a@b.com");
        req.setSubject("subj");
        req.setText("text");

        mockMvc.perform(post("/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith("application/hal+json"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.mail.href").exists());

        verify(mailNotificationService).send("a@b.com", "subj", "text");
    }

    @Test
    void send_shouldFailValidation_onInvalidEmail() throws Exception {
        SendMailRequest req = new SendMailRequest();
        req.setTo("not-email");
        req.setSubject("subj");
        req.setText("text");

        mockMvc.perform(post("/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}

