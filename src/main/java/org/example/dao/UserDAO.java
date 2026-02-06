package org.example.dao;

import org.example.model.User;
import java.util.List;

public interface UserDAO {
    void save(User user);
    User getById(Long id);
    List<User> getAll();
    void update(User user);
    void delete(Long id);
}
