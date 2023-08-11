package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoMapper;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemMapperTest {
    private User user;
    private Item item;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();
    }

    @Test
    public void testToGetItemDtoFromItemWithNullComments() {
        item.setRequestId(1L);

        ItemForResponseDto result = ItemMapper.toGetItemDtoFromItem(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequestId(), result.getRequestId());
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    public void testToGetItemDtoFromItemWithNonEmptyComments() {
        item.setRequestId(1L);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Comment 1");
        comment1.setItem(item);
        comment1.setAuthor(user);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Comment 2");
        comment2.setItem(item);
        comment2.setAuthor(user);

        item.setComments(Set.of(comment1, comment2));

        ItemForResponseDto result = ItemMapper.toGetItemDtoFromItem(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequestId(), result.getRequestId());
        assertNotNull(result.getComments());
        assertEquals(item.getComments().size(), result.getComments().size());

        CommentDto commentDto1 = CommentDtoMapper.toCommentDto(comment1);
        CommentDto commentDto2 = CommentDtoMapper.toCommentDto(comment2);

        assertTrue(result.getComments().contains(commentDto1));
        assertTrue(result.getComments().contains(commentDto2));
    }

    @Test
    public void testToItemWIthBookingDtoWithNoBookings() {
        item.setRequestId(1L);
        item.setBookings(new HashSet<>());

        ItemForResponseDto result = ItemMapper.toItemWIthBookingDto(item);

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertEquals(item.getRequestId(), result.getRequestId());
    }

    @Test
    public void testToItemWIthBookingDtoWithBookings() {
        item.setRequestId(1L);

        Booking booking1 = mock(Booking.class);
        when(booking1.getStart()).thenReturn(LocalDateTime.now().minusDays(1));
        when(booking1.getStatus()).thenReturn(Status.APPROVED);

        Booking booking2 = mock(Booking.class);
        when(booking2.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(booking2.getStatus()).thenReturn(Status.APPROVED);

        Set<Booking> bookings = new HashSet<>();
        bookings.add(booking1);
        bookings.add(booking2);

        item.setBookings(bookings);

        ItemForResponseDto result = ItemMapper.toItemWIthBookingDto(item);

        assertEquals(BookingMapper.toBookingForItemDto(booking1), result.getLastBooking());
        assertEquals(BookingMapper.toBookingForItemDto(booking2), result.getNextBooking());
        assertEquals(item.getRequestId(), result.getRequestId());
    }

    @Test
    public void testToGetItemFromCreateUpdateItemDto() {
        CreateUpdateItemDto dto = new CreateUpdateItemDto();
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        Item result = ItemMapper.toGetItemFromCreateUpdateItemDto(dto);

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getAvailable(), result.getAvailable());
        assertEquals(dto.getRequestId(), result.getRequestId());
    }

    @Test
    public void testToGetBookingDtoFromItem() {
        ItemWithBookingDto result = ItemMapper.toGetBookingDtoFromItem(item);

        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    public void testToItemFromItemRequest() {
        ItemForResponseDto dto = ItemForResponseDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        Item result = ItemMapper.toItemFromItemRequest(dto);

        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getRequestId(), result.getRequestId());
    }
}
