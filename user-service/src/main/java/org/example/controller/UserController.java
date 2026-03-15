package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Users", description = "CRUD для пользователей")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    private EntityModel<UserResponseDto> toModel(UserResponseDto user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).getById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("all"),
                linkTo(methodOn(UserController.class).update(user.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(user.id())).withRel("delete")
        );
    }

    @Operation(summary = "Создать пользователя")
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDto>> create(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto created = service.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toModel(created));
    }

    @Operation(summary = "Получить пользователя по id")
    @GetMapping("/{id}")
    public EntityModel<UserResponseDto> getById(@PathVariable Long id) {
        return toModel(service.getById(id));
    }

    @Operation(summary = "Получить список пользователей")
    @GetMapping
    public CollectionModel<EntityModel<UserResponseDto>> getAll() {
        List<EntityModel<UserResponseDto>> models = service.getAll().stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(
                models,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel()
        );
    }

    @Operation(summary = "Обновить пользователя")
    @PutMapping("/{id}")
    public EntityModel<UserResponseDto> update(@PathVariable Long id, @Valid @RequestBody UserRequestDto dto) {
        return toModel(service.update(id, dto));
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}