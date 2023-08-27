package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.user.dto.UserOnlyWithIdDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testBookingForItemDtoSerialization() throws JsonProcessingException {
        BookingForItemDto dto = BookingForItemDto.builder()
                .id(1L)
                .bookerId(2L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1,\"bookerId\":2}");
    }

    @Test
    public void testUserOnlyWithIdDtoSerialization() throws JsonProcessingException {
        UserOnlyWithIdDto dto = UserOnlyWithIdDto.builder().id(2L).build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":2}");
    }

    @Test
    public void testCreateBookingDtoSerialization() throws JsonProcessingException {
        CreateBookingDto dto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2022, 1, 1, 10, 0))
                .end(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"itemId\":1,\"start\":\"2022-01-01T10:00:00\",\"end\":\"2022-01-01T12:00:00\"}");
    }

    @Test
    public void testCreateBookingDtoDeserialization() throws IOException {
        String json = "{\"itemId\":1,\"start\":\"2022-01-01T10:00:00\",\"end\":\"2022-01-01T12:00:00\"}";

        CreateBookingDto dto = objectMapper.readValue(json, CreateBookingDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2022, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2022, 1, 1, 12, 0));
    }
}
