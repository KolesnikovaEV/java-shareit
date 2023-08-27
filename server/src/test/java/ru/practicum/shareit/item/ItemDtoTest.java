package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForItemRequest;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUpdateItemDtoDeserialization() throws Exception {
        String json = "{\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":1}";

        CreateUpdateItemDto dto = objectMapper.readValue(json, CreateUpdateItemDto.class);

        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getRequestId()).isEqualTo(1L);
    }

    @Test
    public void testItemDtoForItemRequestSerialization() throws Exception {
        ItemDtoForItemRequest dto = ItemDtoForItemRequest.builder()
                .id(1L)
                .name("Test Item")
                .requestId(1L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1,\"name\":\"Test Item\",\"requestId\":1}");
    }

    @Test
    public void testItemForResponseDtoSerialization() throws Exception {
        ItemForResponseDto dto = ItemForResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\"," +
                "\"available\":true,\"lastBooking\":null,\"nextBooking\":null,\"comments\":null,\"requestId\":1}");
    }

    @Test
    public void testItemWithBookingDtoSerialization() throws Exception {
        ItemWithBookingDto dto = ItemWithBookingDto.builder()
                .id(1L)
                .name("Test Item")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1,\"name\":\"Test Item\"}");
    }
}
