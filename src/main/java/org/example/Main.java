package org.example;

import org.example.dao.UserDAOImpl;
import org.example.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final UserDAOImpl userDAO = new UserDAOImpl();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Выберите операцию: 1 - Создать, 2 - Получить, 3 - Получить всех, 4 - Обновить, 5 - Удалить, 0 - Выход");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Create user
                    User user = new User();
                    System.out.print("Введите имя: ");
                    user.setName(scanner.next());
                    System.out.print("Введите email: ");
                    user.setEmail(scanner.next());
                    System.out.print("Введите возраст: ");
                    user.setAge(scanner.nextInt());
                    user.setCreatedAt(LocalDateTime.now());
                    userDAO.save(user);
                    break;
                case 2:
                    // Get user by ID
                    System.out.print("Введите ID пользователя: ");
                    User foundUser = userDAO.getById(scanner.nextLong());
                    System.out.println(foundUser);
                    break;
                case 3:
                    // Get all users
                    List<User> users = userDAO.getAll();
                    users.forEach(System.out::println);
                    break;
                case 4:
                    // Update user
                    System.out.print("Введите ID пользователя для обновления: ");
                    Long updateId = scanner.nextLong();
                    User updateUser = userDAO.getById(updateId);
                    if (updateUser != null) {
                        System.out.print("Новое имя: ");
                        updateUser.setName(scanner.next());
                        System.out.print("Новый email: ");
                        updateUser.setEmail(scanner.next());
                        System.out.print("Новый возраст: ");
                        updateUser.setAge(scanner.nextInt());
                        userDAO.update(updateUser);
                    }
                    break;
                case 5:
                    // Delete user
                    System.out.print("Введите ID пользователя для удаления: ");
                    userDAO.delete(scanner.nextLong());
                    break;
                case 0:
                    // Exit
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор");
                    break;
            }
        }
    }
}