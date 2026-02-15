import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequestDto;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void create_then_getById() throws Exception {
        UserRequestDto req = new UserRequestDto();
        req.setName("Ivan");
        req.setEmail("ivan@example.com");
        req.setAge(25);

        String created = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void update() throws Exception {
        UserRequestDto req = new UserRequestDto();
        req.setName("Ivan");
        req.setEmail("ivan@example.com");
        req.setAge(25);

        long id = objectMapper.readTree(
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andReturn().getResponse().getContentAsString()
        ).get("id").asLong();

        UserRequestDto upd = new UserRequestDto();
        upd.setName("Petr");
        upd.setEmail("petr@example.com");
        upd.setAge(30);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Petr"))
                .andExpect(jsonPath("$.email").value("petr@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void delete_then_get_404() throws Exception {
        UserRequestDto req = new UserRequestDto();
        req.setName("Ivan");
        req.setEmail("ivan@example.com");
        req.setAge(25);

        long id = objectMapper.readTree(
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andReturn().getResponse().getContentAsString()
        ).get("id").asLong();

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("User not found")));
    }

    @Test
    void validation_400() throws Exception {
        UserRequestDto req = new UserRequestDto();
        req.setName("");          // invalid
        req.setEmail("bad");      // invalid
        req.setAge(-10);          // invalid

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.name", notNullValue()))
                .andExpect(jsonPath("$.fields.email", notNullValue()))
                .andExpect(jsonPath("$.fields.age", notNullValue()));
    }
}