package org.example.mapper;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toEntity_nullDto_returnsNull() {
        assertNull(UserMapper.toEntity(null));
    }

    @Test
    void toEntity_mapsFields() {
        UserRequestDto dto = new UserRequestDto("Alice", "alice@example.com", 20);

        User user = UserMapper.toEntity(dto);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(20, user.getAge());
    }

    @Test
    void apply_nullUser_doesNothing() {
        UserRequestDto dto = new UserRequestDto("Bob", "bob@example.com", 30);
        assertDoesNotThrow(() -> UserMapper.apply(null, dto));
    }

    @Test
    void apply_nullDto_doesNothing() {
        User user = new User();
        user.setName("Old");
        user.setEmail("old@example.com");
        user.setAge(10);

        assertDoesNotThrow(() -> UserMapper.apply(user, null));

        assertEquals("Old", user.getName());
        assertEquals("old@example.com", user.getEmail());
        assertEquals(10, user.getAge());
    }

    @Test
    void apply_updatesExistingEntity() {
        User user = new User();
        user.setId(1L);
        user.setName("Old");
        user.setEmail("old@example.com");
        user.setAge(10);

        UserRequestDto dto = new UserRequestDto("New", "new@example.com", 99);
        UserMapper.apply(user, dto);

        assertEquals(1L, user.getId()); // id не трогаем
        assertEquals("New", user.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals(99, user.getAge());
    }

    @Test
    void toDto_nullEntity_returnsNull() {
        assertNull(UserMapper.toDto(null));
    }

    @Test
    void toDto_mapsFields() {
        User user = new User();
        user.setId(7L);
        user.setName("Ann");
        user.setEmail("ann@example.com");
        user.setAge(22);

        UserResponseDto dto = UserMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(7L, dto.id());
        assertEquals("Ann", dto.name());
        assertEquals("ann@example.com", dto.email());
        assertEquals(22, dto.age());
    }

    @Test
    void toDtoList_null_returnsEmptyList() {
        assertNotNull(UserMapper.toDtoList(null));
        assertTrue(UserMapper.toDtoList(null).isEmpty());
    }

    @Test
    void toDtoList_empty_returnsEmptyList() {
        assertTrue(UserMapper.toDtoList(List.of()).isEmpty());
    }

    @Test
    void toDtoList_filtersNullElements() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("A");
        u1.setEmail("a@ex.com");
        u1.setAge(10);

        List<UserResponseDto> result = UserMapper.toDtoList(List.of(u1, null));
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void toEntityList_null_returnsEmptyList() {
        assertTrue(UserMapper.toEntityList(null).isEmpty());
    }

    @Test
    void toEntityList_empty_returnsEmptyList() {
        assertTrue(UserMapper.toEntityList(List.of()).isEmpty());
    }

    @Test
    void toEntityList_filtersNullElements() {
        UserRequestDto d1 = new UserRequestDto("A", "a@ex.com", 1);

        List<User> result = UserMapper.toEntityList(List.of(d1, null));

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getName());
    }
}