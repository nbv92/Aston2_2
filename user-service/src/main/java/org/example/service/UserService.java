package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserRequestDto dto);
    UserResponseDto getById(Long id);
    List<UserResponseDto> getAll();
    UserResponseDto update(Long id, UserRequestDto dto);
    void delete(Long id);
}
