import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.event.UserEvent;
import org.example.kafka.UserEventPublisher;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserNotFoundException;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher publisher;

    public UserServiceImpl(UserRepository userRepository, UserEventPublisher publisher) {
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);

        publishAfterCommit(new UserEvent(UserEvent.Operation.CREATED, saved.getEmail()));

        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // если email меняется — обычно тоже проверяют уникальность
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());

        User saved = userRepository.save(user);

        // по желанию:
        // publishAfterCommit(new UserEvent(UserEvent.Operation.UPDATED, saved.getEmail()));

        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        String email = user.getEmail();
        userRepository.delete(user);publishAfterCommit(new UserEvent(UserEvent.Operation.DELETED, email));
    }

    private void publishAfterCommit(UserEvent event) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            publisher.publish(event);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publisher.publish(event);
            }
        });
    }
}