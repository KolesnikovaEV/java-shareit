package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ValidationService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final UserMapper mapper;

    public UserServiceImpl(@Qualifier("InMemory") UserRepository userRepository, UserMapper mapper,
                           ValidationService validationService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.validationService = validationService;
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("User {} is found", id);
        validationService.isExistUser(id);
        UserDto getUser = mapper.toUserDto(userRepository.getUserById(id));
        return getUser;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsersDto = new ArrayList<>();
        List<User> allUsers = userRepository.getAllUsers();
        allUsers.stream().map(mapper::toUserDto).forEach(allUsersDto::add);
        log.info("All users are shown");
        return allUsersDto;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating User");
        User user = mapper.toUser(userDto);
        validationService.validateUserFields(user);
        UserDto createdUser = mapper.toUserDto(userRepository.createUser(user));
        log.info("User is created");
        return createdUser;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        userDto.setId(userId);
        User user = mapper.toUser(userDto);
        validationService.isExistUser(userId);
        boolean[] isUpdateFields = validationService.checkFieldsForUpdate(user);
        log.info("User {} is updated", userId);
        User updatedUser = userRepository.updateUser(user, isUpdateFields);
        return mapper.toUserDto(updatedUser);
    }

    @Override
    public void removeUser(Long id) {
        validationService.isExistUser(id);
        log.info("User {} is removed", id);
        userRepository.removeUser(id);
    }

}
