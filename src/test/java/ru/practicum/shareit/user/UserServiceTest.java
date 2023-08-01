package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.validation.ValidationService;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidationService validationService;

    CreateUpdateUserDto userDto1, userDto2;
    User user1, user2;

    @BeforeEach
    void setUp() {
        userDto1 = CreateUpdateUserDto.builder()
                .name("user1")
                .email("user1@ya.ru")
                .build();
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@ya.ru")
                .build();

        userDto2 = CreateUpdateUserDto.builder()
                .name("user2")
                .email("user2@ya.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@ya.ru").build();

    }

    @Test
    void getAllUsers_WhenUsersExist_ThenReturnListOfUsers() {
        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserForResponseDto> usersForResponse = userService.getAllUsers();

        assertNotNull(usersForResponse);
        assertEquals(2, usersForResponse.size());

        UserForResponseDto userDto1 = usersForResponse.get(0);
        assertEquals(user1.getId(), userDto1.getId());
        assertEquals(user1.getName(), userDto1.getName());
        assertEquals(user1.getEmail(), userDto1.getEmail());

        UserForResponseDto userDto2 = usersForResponse.get(1);
        assertEquals(user2.getId(), userDto2.getId());
        assertEquals(user2.getName(), userDto2.getName());
        assertEquals(user2.getEmail(), userDto2.getEmail());
    }

    @Test
    void getUserById_WhenUserExist_ThenReturnUser() {
        when(validationService.isExistUser(user1.getId())).thenReturn(user1);

        UserForResponseDto userForResponseDto = userService.getUserById(1L);

        assertNotNull(userForResponseDto);
        assertEquals(user1.getId(), userForResponseDto.getId());
        assertEquals(user1.getName(), userForResponseDto.getName());
        assertEquals(user1.getEmail(), userForResponseDto.getEmail());
    }

    @Test
    public void testRemoveUser() {
        Long userId = 1L;

        when(validationService.isExistUser(userId)).thenReturn(user1);

        userService.removeUser(userId);

        Mockito.verify(validationService).isExistUser(userId);
        Mockito.verify(userRepository).deleteById(userId);
    }

    @Test
    public void testCreateUser_WhenValidUser_ThenReturnCreatedUser() {
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserForResponseDto createdUser = userService.createUser(userDto1);

        assertNotNull(createdUser);
        assertEquals(user1.getId(), createdUser.getId());
        assertEquals(user1.getName(), createdUser.getName());
        assertEquals(user1.getEmail(), createdUser.getEmail());
    }

    @Test
    void createUser_WhenInvalidUser_ThenThrowException() {
        user1.setId(null);
        when(userRepository.save(user1)).thenThrow(new ConstraintViolationException("Invalid user", null));

        assertThrows(ConstraintViolationException.class, () -> userService.createUser(userDto1));

        verify(userRepository).save(user1);
    }

    @Test
    void updateUser_WhenValidUser_ThenReturnUpdatedUser() {
        Long userId = 1L;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updateduser@example.com");

        when(validationService.isExistUser(userId)).thenReturn(user1);
        when(userRepository.saveAndFlush(user1)).thenReturn(updatedUser);

        UserForResponseDto expectedResponse = UserMapper.userForResponseDto(updatedUser);
        UserForResponseDto actualResponse = userService.updateUser(userId, userDto1);

        assertEquals(expectedResponse, actualResponse);
        verify(validationService).isExistUser(userId);
        verify(userRepository).saveAndFlush(user1);
    }

    @Test
    void updateUser_WhenDataIntegrityViolationException_ThenThrowConflictException() {
        Long userId = 1L;

        when(validationService.isExistUser(userId)).thenReturn(user1);
        when(userRepository.saveAndFlush(user1)).thenThrow(
                new DataIntegrityViolationException("Data integrity violation"));

        assertThrows(ConflictException.class, () -> userService.updateUser(userId, userDto1));

        verify(validationService).isExistUser(userId);
        verify(userRepository).saveAndFlush(user1);
    }

    @Test
    void testHashCode() {
        User user1 = User.builder()
                .id(1L)
                .name("name")
                .email("Email.ddsa.z").build();

        User user2 = User.builder()
                .id(1L)
                .name("name")
                .email("Email.ddsa.z").build();

        User user3 = User.builder()
                .id(1L)
                .name("23")
                .email("Email.4.z").build();

        assertEquals(user1, user2);
        assertEquals(user1, user3);
    }
}
