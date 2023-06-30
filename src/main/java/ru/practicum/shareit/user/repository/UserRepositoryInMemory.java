package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("InMemory")
@Slf4j
public class UserRepositoryInMemory implements UserRepository {
    private Map<Long, User> userRepository = new HashMap<>();
    private Long generateId = 1L;

    @Override
    public User createUser(User user) {
        user.setId(generateId++);
        userRepository.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Finding user {}", id);
        log.info("User {} is founded", id);
        return userRepository.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public User updateUser(User userToUpdate, boolean[] isUpdateFields) {
        final Long inputId = userToUpdate.getId();
        final String inputName = userToUpdate.getName();
        final String inputEmail = userToUpdate.getEmail();

        User oldUser = userRepository.get(inputId);

        if (isUpdateFields[0]) {
            oldUser.setName(inputName);
        }
        if (isUpdateFields[1]) {
            oldUser.setEmail(inputEmail);
        }
        userRepository.put(inputId, oldUser);
        log.info("User updated: {}", oldUser);

        return oldUser;
    }

    @Override
    public void removeUser(Long id) {
        try {
            userRepository.remove(id);
        } catch (NotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public Long getUserIdByEmail(String email) {
        if (email == null) {
            return null;
        }
        for (User user : userRepository.values()) {
            String userEmail = user.getEmail();
            if (email.equals(userEmail)) {
                return user.getId();
            }
        }
        return null;
    }
}
