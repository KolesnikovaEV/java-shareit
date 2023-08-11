package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponse;
import ru.practicum.shareit.user.dto.UserForResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testItemRequestDtoDeserialization() throws Exception {
        String json = "{\"description\":\"Test Description\"}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getDescription()).isEqualTo("Test Description");
    }

    @Test
    public void testItemRequestForResponseSerialization() throws Exception {
        LocalDateTime created = LocalDateTime.of(2022, 1, 1, 10, 0);

        ItemForResponseDto itemDto = ItemForResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .build();

        UserForResponseDto userDto = UserForResponseDto.builder()
                .id(1L)
                .name("testuser")
                .build();

        ItemRequestForResponse dto = ItemRequestForResponse.builder()
                .id(1L)
                .description("Test Description")
                .requester(userDto)
                .created(created)
                .items(List.of(itemDto))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1,\"description\":\"Test Description\"," +
                "\"requester\":{\"id\":1,\"name\":\"testuser\",\"email\":null}," +
                "\"created\":\"2022-01-01T10:00:00\",\"items\":[{\"id\":1,\"name\":\"Test Item\"," +
                "\"description\":\"Test Description\",\"available\":true,\"lastBooking\":null,\"nextBooking\":null," +
                "\"comments\":null,\"requestId\":1}]}");
    }
}
