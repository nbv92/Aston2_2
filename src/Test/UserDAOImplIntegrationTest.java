package Test;


import org.example.dao.UserDAO;
import org.example.dao.UserDAOImpl;
import org.example.model.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)

public class UserDAOImplIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private UserDAO userDAO;

    @BeforeAll
    static void startContainer() {
        postgres.start();

        // HibernateUtil должен использовать параметры контейнера для подключения к БД
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
    }

    @AfterAll
    static void stopContainer() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }

    @Test
    void testSaveAndGetById() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setAge(25);

        userDAO.save(user);
        assertNotNull(user.getId());

        Optional<User> retrieved = userDAO.getById(user.getId());
        assertEquals("Alice", retrieved.get().getName());
        assertEquals("alice@example.com", retrieved.get().getEmail());
    }

    @Test
    void testGetAll() {
        User user1 = new User();
        user1.setName("Bob");
        user1.setEmail("bob@example.com");
        user1.setAge(30);
        userDAO.save(user1);

        User user2 = new User();
        user2.setName("Charlie");
        user2.setEmail("charlie@example.com");
        user2.setAge(35);
        userDAO.save(user2);

        List<User> users = userDAO.getAll();
        assertTrue(users.size() >= 2); // Может быть больше, если не очищать БД между тестами
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setName("David");
        user.setEmail("david@example.com");
        user.setAge(40);
        userDAO.save(user);

        user.setName("David Updated");
        userDAO.update(user);

        Optional<User> updated = userDAO.getById(user.getId());
        assertEquals("David Updated", updated.get().getName());
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setName("Eve");
        user.setEmail("eve@example.com");
        user.setAge(28);
        userDAO.save(user);

        Long id = user.getId();
        userDAO.delete(id);

        Optional<User> deleted = userDAO.getById(id);
        assertNull(deleted);
    }
}
