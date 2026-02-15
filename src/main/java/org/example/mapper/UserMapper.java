package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;

public final class UserMapper {
    private UserMapper() {}

    public static User toEntity(UserRequestDto dto) {
        User user = new User();
        apply(user, dto);
        return user;
    }

    public static void apply(User user, UserRequestDto dto) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
    }

    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }
}