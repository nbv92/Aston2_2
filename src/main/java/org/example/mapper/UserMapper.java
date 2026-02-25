package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class UserMapper {
    private UserMapper() {}

    public static User toEntity(UserRequestDto dto) {
        if (dto == null) return null;
        User user = new User();
        apply(user, dto);
        return user;
    }

    public static void apply(User user, UserRequestDto dto) {
        if (user == null || dto == null) return;

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
    }


    public static UserResponseDto toDto(User user) {
        if (user == null) return null;
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }

    public static List<UserResponseDto> toDtoList(List<User> users) {
        if (users == null || users.isEmpty()) return Collections.emptyList();
        return users.stream()
                .filter(Objects::nonNull)
                .map(UserMapper::toDto)
                .toList();
    }

    public static List<User> toEntityList(List<UserRequestDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return Collections.emptyList();
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(UserMapper::toEntity)
                .toList();
    }
}