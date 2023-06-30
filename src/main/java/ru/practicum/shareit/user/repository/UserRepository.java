package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(User user, boolean[] isUpdateFields);

    void removeUser(Long id);

    Long getUserIdByEmail(String email);
}
