package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;

import java.util.List;

public interface UserService {
    UserForResponseDto getUserById(Long id);

    List<UserForResponseDto> getAllUsers();

    UserForResponseDto createUser(CreateUpdateUserDto createUpdateUserDto);

    UserForResponseDto updateUser(Long userId, CreateUpdateUserDto createUpdateUserDto);

    void removeUser(Long id);

}
