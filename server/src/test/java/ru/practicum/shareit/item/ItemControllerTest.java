package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;

    @MockBean
    ItemRepository itemRepository;

    @Autowired
    MockMvc mockMvc;
    User user1, user2;
    Item item, item2;
    ItemForResponseDto itemForResponseDto;
    BookingForItemDto bookingForItemDto1, bookingForItemDto2;
    CreateUpdateItemDto createUpdateItemDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@ya.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user2)
                .build();
        createUpdateItemDto = CreateUpdateItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        bookingForItemDto1 = BookingForItemDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();

        bookingForItemDto2 = BookingForItemDto.builder()
                .id(2L)
                .bookerId(2L)
                .build();
        itemForResponseDto = ItemForResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(bookingForItemDto1)
                .nextBooking(bookingForItemDto2)
                .comments(new ArrayList<>())
                .build();
    }

    @SneakyThrows
    @Test
    void testGetItemById() {
        when(itemService.getItemById(any(), any()))
                .thenReturn(itemForResponseDto);

        mockMvc.perform(get("/items/{id}", itemForResponseDto.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testGetAllByUserId() {
        when(itemService.getAllItems(any()))
                .thenReturn(List.of(itemForResponseDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestId", is(item.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testSearchItems() {
        when(itemService.searchItem(any(), eq("found one item")))
                .thenReturn(List.of(itemForResponseDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "found one item")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemForResponseDto))));

        when(itemService.searchItem(eq(1L), eq("items not found")))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "items not found")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }

    @SneakyThrows
    @Test
    void testCreateItem() {
        when(itemService.createItem(any(), any()))
                .thenReturn(itemForResponseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(item.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testUpdateItem_whenAllAreOk_aAndReturnUpdatedItem() {
        when(itemService.updateItem(any(), any(), any()))
                .thenReturn(itemForResponseDto);

        mockMvc.perform(patch("/items/{itemId}", itemForResponseDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemForResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemForResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemForResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemForResponseDto.getName()), String.class));
    }

    @SneakyThrows
    @Test
    void testUpdateItem_whenAllAreNotOk_aAndReturnExceptionNotFoundRecordInBD() {
        when(itemService.updateItem(any(), any(), any()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createComment_whenAllIsOk_returnSavedComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("name user")
                .created(LocalDateTime.now().minusSeconds(5)).build();
        when(itemService.createComment(any(), any(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));
    }
}
