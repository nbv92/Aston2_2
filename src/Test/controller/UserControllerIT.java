package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService service;

    @Test
    void create_validRequest_returns201AndBody() throws Exception {
        UserRequestDto req = new UserRequestDto("Alice", "alice@example.com", 20);
        UserResponseDto resp = new UserResponseDto(1L, "Alice", "alice@example.com", 20);

        when(service.create(ArgumentMatchers.any(UserRequestDto.class))).thenReturn(resp);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.age").value(20));

        verify(service, times(1)).create(any(UserRequestDto.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    void create_invalidRequest_returns400_andServiceNotCalled() throws Exception {
        // name blank, email invalid, age null -> нарушаем @Valid
        String badJson = """
                {"name":"", "email":"not-an-email", "age":null}
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void getById_returns200AndBody() throws Exception {
        UserResponseDto resp = new UserResponseDto(5L, "Bob", "bob@example.com", 33);
        when(service.getById(5L)).thenReturn(resp);

        mockMvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"))
                .andExpect(jsonPath("$.age").value(33));

        verify(service).getById(5L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void getAll_returns200AndList() throws Exception {
        when(service.getAll()).thenReturn(List.of(new UserResponseDto(1L, "A", "a@ex.com", 10),
                new UserResponseDto(2L, "B", "b@ex.com", 20)
        ));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(service).getAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void update_validRequest_returns200AndBody() throws Exception {
        UserRequestDto req = new UserRequestDto("NewName", "new@example.com", 44);
        UserResponseDto resp = new UserResponseDto(10L, "NewName", "new@example.com", 44);

        when(service.update(eq(10L), any(UserRequestDto.class))).thenReturn(resp);

        mockMvc.perform(put("/api/users/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.age").value(44));

        verify(service).update(eq(10L), any(UserRequestDto.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    void update_invalidRequest_returns400_andServiceNotCalled() throws Exception {
        String badJson = """
                {"name":" ", "email":"bad", "age":-1}
                """;

        mockMvc.perform(put("/api/users/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(service).delete(3L);

        mockMvc.perform(delete("/api/users/3"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(3L);
        verifyNoMoreInteractions(service);
    }
}