package ru.practicum.gate.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gate.validation.CreateObject;
import ru.practicum.gate.validation.UpdateObject;
import ru.practicum.gate.user.dto.CreateUpdateUserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting All Users");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid @Validated(CreateObject.class)
                                             CreateUpdateUserDto createUpdateUserDto) {
        log.info("Creating User");
        return userClient.createUser(createUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                             @Validated(UpdateObject.class) @Valid
                             @RequestBody CreateUpdateUserDto userToUpdate) {
        log.info("Updating User: {}", userId);
        return userClient.updateUser(userId, userToUpdate);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Getting User by User ID: {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Deleting User: {}", userId);
        return userClient.removeUser(userId);
    }
}
