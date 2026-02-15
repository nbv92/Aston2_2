package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserResponseDto create(UserRequestDto dto) {
        User saved = repo.save(UserMapper.toEntity(dto));
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        User user = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public UserResponseDto update(Long id, UserRequestDto dto) {
        User user = repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        UserMapper.apply(user, dto);
        User saved = repo.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repo.deleteById(id);
    }
}