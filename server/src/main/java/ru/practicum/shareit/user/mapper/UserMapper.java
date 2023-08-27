package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.dto.UserOnlyWithIdDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public UserForResponseDto userForResponseDto(User user) {
        return UserForResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUserFromCreateUpdateUserDto(CreateUpdateUserDto createUpdateUserDto) {
        return User.builder()
                .name(createUpdateUserDto.getName())
                .email(createUpdateUserDto.getEmail())
                .build();
    }

    public UserOnlyWithIdDto userOnlyWithIdDto(User user) {
        return UserOnlyWithIdDto.builder()
                .id(user.getId())
                .build();
    }
}