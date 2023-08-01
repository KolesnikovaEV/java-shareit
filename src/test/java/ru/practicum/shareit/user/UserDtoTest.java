package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.CreateUpdateUserDto;
import ru.practicum.shareit.user.dto.UserForResponseDto;
import ru.practicum.shareit.user.dto.UserOnlyWithIdDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUpdateUserDtoSerialization() throws JsonProcessingException {
        CreateUpdateUserDto dto = CreateUpdateUserDto.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}");
    }

    @Test
    public void testCreateUpdateUserDtoDeserialization() throws IOException {
        String json = "{\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}";

        CreateUpdateUserDto dto = objectMapper.readValue(json, CreateUpdateUserDto.class);

        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("johndoe@example.com");
    }

    @Test
    public void testUserForResponseDtoSerialization() throws JsonProcessingException {
        UserForResponseDto dto = UserForResponseDto.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}");
    }

    @Test
    public void testUserOnlyWithIdDtoSerialization() throws JsonProcessingException {
        UserOnlyWithIdDto dto = UserOnlyWithIdDto.builder()
                .id(1L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1}");
    }
}
