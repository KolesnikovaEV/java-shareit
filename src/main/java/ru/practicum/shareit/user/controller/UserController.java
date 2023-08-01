package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserForResponseDto> getAllUsers() {
        log.info("Getting All Users");
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<UserForResponseDto> createUser(@RequestBody @Valid @Validated(CreateObject.class)
                                                         CreateUpdateUserDto createUpdateUserDto) {
        log.info("Creating User");
        return ResponseEntity.ok(userService.createUser(createUpdateUserDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserForResponseDto> updateUser(@PathVariable Long userId,
                                                         @Validated(UpdateObject.class) @Valid
                                                         @RequestBody CreateUpdateUserDto userToUpdate) {
        log.info("Updating User: {}", userId);
        return ResponseEntity.ok(userService.updateUser(userId, userToUpdate));
    }

    @GetMapping("/{userId}")
    public UserForResponseDto getUserById(@PathVariable Long userId) {
        log.info("Getting User by User ID: {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Deleting User: {}", userId);
        userService.removeUser(userId);
    }

}
