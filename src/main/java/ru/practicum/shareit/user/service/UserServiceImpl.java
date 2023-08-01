package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ValidationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationService validationService;

    @Override
    @Transactional(readOnly = true)
    public UserForResponseDto getUserById(Long id) {
        User result = validationService.isExistUser(id);

        log.info("User {} is found", id);
        return UserMapper.userForResponseDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserForResponseDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        log.info("All users are shown");
        return allUsers.stream().map(UserMapper::userForResponseDto).collect(Collectors.toList());
    }

    @Override
    public UserForResponseDto createUser(CreateUpdateUserDto createUpdateUserDto) {
        log.info("Creating User");
        User user = UserMapper.toUserFromCreateUpdateUserDto(createUpdateUserDto);
        validationService.validateUserFields(user);
        try {
            return UserMapper.userForResponseDto(userRepository.save(
                    UserMapper.toUserFromCreateUpdateUserDto(createUpdateUserDto)));
        } catch (ConstraintViolationException e) {
            throw new ConflictException("User cannot be created");
        }
    }

    @Override
    public UserForResponseDto updateUser(Long userId, CreateUpdateUserDto createUpdateUserDto) {
        User userForUpdate = validationService.isExistUser(userId);
        if (createUpdateUserDto.getName() != null && !createUpdateUserDto.getName().isBlank()) {
            userForUpdate.setName(createUpdateUserDto.getName());
        }
        if (createUpdateUserDto.getEmail() != null && !createUpdateUserDto.getEmail().isBlank()) {
            userForUpdate.setEmail(createUpdateUserDto.getEmail());
        }
        try {
            return UserMapper.userForResponseDto(userRepository.saveAndFlush(userForUpdate));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("User cannot be updated");
        }
    }

    @Override
    public void removeUser(Long id) {
        validationService.isExistUser(id);

        log.info("User {} is removed", id);
        userRepository.deleteById(id);
    }

}
