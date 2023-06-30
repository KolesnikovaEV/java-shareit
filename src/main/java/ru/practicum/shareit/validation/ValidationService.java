package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
public class ValidationService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ValidationService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public void checkUniqueEmailToCreate(User user) {
        final String inputEmail = user.getEmail();

        Long idByEmail = userRepository.getUserIdByEmail(inputEmail);
        //Надо проверить уникальность почты.
        if (idByEmail != null) {
            String message = String.format("Updating User's email is impossible, email = %s " +
                    "belongs to User ID: %s.", inputEmail, userRepository.getUserById(idByEmail));
            log.info(message);
            throw new ConflictException(message);
        }
    }

    public void checkUniqueEmailToUpdate(User user) {
        final Long inputId = user.getId();
        final String inputEmail = user.getEmail();

        if (inputId == null) {
            String message = "Updating User is impossible, User ID cannot be Null.";
            log.info(message);
            throw new NotFoundException(message);
        }

        final Long idByEmail = userRepository.getUserIdByEmail(inputEmail);
        if (idByEmail != null && !inputId.equals(idByEmail)) {
            String message = String.format("Updating User's email is impossible, email = %s " +
                    "belongs to User ID = %d.", inputEmail, idByEmail);
            log.info(message + " Incoming user: " + user);
            throw new ConflictException(message);
        }
    }

    public User isExistUser(Long userId) {
        User result = userRepository.getUserById(userId);
        if (result == null) {
            String error = String.format("User ID = '%d' not found.", userId);
            log.info(error);
            throw new NotFoundException(error);
        }
        return result;
    }

    public void validateUserFields(User user) {
        log.info("Validation is going.");
        final String email = user.getEmail();
        if (email == null || email.isBlank() || !email.contains("@")) {
            String error = "Email cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
        checkUniqueEmailToCreate(user);
        final String name = user.getName();
        if (name == null || name.isBlank()) {
            String error = "Name cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
    }

    public boolean[] checkFieldsForUpdate(User user) {
        boolean[] result;
        result = new boolean[2];
        final String name = user.getName();
        final String email = user.getEmail();
        result[0] = (name != null) && !name.isBlank();
        result[1] = (email != null) && !email.isBlank();
        if (email != null) {
            checkUniqueEmailToUpdate(user);
        }

        if (result[0] || result[1]) {
            return result;
        }
        throw new ValidationException("All fields are 'null'.");
    }

    public void validateItemFields(Item item) {
        final String name = item.getName();
        final String description = item.getDescription();
        if (name == null || name.isBlank()) {
            String error = "Item name cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
        if (description == null || description.isBlank()) {
            String error = "Item description cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
        final Boolean available = item.getAvailable();
        if (available == null) {
            String error = "Item availability cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
        final Long ownerId = item.getOwner();
        if (ownerId == null) {
            String error = "Item ownerId cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
    }

    public boolean[] checkFieldsForUpdateItem(Item item) {
        boolean[] result = new boolean[3];
        result[0] = (item.getName() != null) && (!item.getName().isBlank());
        result[1] = (item.getDescription() != null) && !item.getDescription().isBlank();
        result[2] = item.getAvailable() != null;
        for (boolean b : result) {
            if (b) return result;
        }
        throw new ValidationException("All Item fields are 'null'.");
    }

    public Item isExistItem(long itemId) {
        Item result = itemRepository.getItemById(itemId);
        if (result == null) {
            String error = String.format("Item ID = '%d' not found.", itemId);
            log.info(error);
            throw new NotFoundException(error);
        }
        return result;
    }

    public boolean isOwnerItem(Item item, Long ownerId) {
        if (item == null || ownerId == null) {
            String message = "Item or owner ID is null.";
            log.info("Error 400. {}", message);
            throw new ValidationException(message);
        }
        if (!ownerId.equals(item.getOwner())) {
            String message = String.format("Item %s doesn't belong to User ID = %d.", item.getName(), ownerId);
            log.info(message);
            throw new NotFoundException(message);
        }
        return true;
    }
}
