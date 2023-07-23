package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserForResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserForResponseDto createUser(@RequestBody @Valid @Validated(CreateObject.class)
                                         CreateUpdateUserDto createUpdateUserDto) {
        return userService.createUser(createUpdateUserDto);
    }

    @PatchMapping("/{userId}")
    public UserForResponseDto updateUser(@PathVariable Long userId,
                                         @Validated(UpdateObject.class) @Valid
                                         @RequestBody CreateUpdateUserDto userToUpdate) {
        return userService.updateUser(userId, userToUpdate);
    }

    @GetMapping("/{userId}")
    public UserForResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.removeUser(userId);
    }

}
