package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ValidationService validationService;
    @Mock
    private CommentRepository commentRepository;
    private User user1;
    private Item item, item2;
    private CreateUpdateItemDto createUpdateItemDto;
    private Booking booking1, booking2;
    private static LocalDateTime start;
    private static LocalDateTime end;

    @BeforeAll
    static void beforeAll() {
        start = LocalDateTime.now();
        end = LocalDateTime.now();
    }

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
                .owner(user1)
                .build();
        createUpdateItemDto = CreateUpdateItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user1)
                .status(WAITING)
                .build();
        booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item2)
                .booker(user1)
                .status(WAITING)
                .build();
    }

    @Test
    void testGetAllItemsWithBookings() {
        Long userId = 1L;

        item.setId(1L);
        item.setOwner(user1);
        item.setBookings(Set.of(booking1));

        item2.setId(2L);
        item2.setOwner(user1);
        item2.setBookings(Set.of(booking2));

        List<Item> allItems = List.of(item, item2);

        when(validationService.isExistUser(userId)).thenReturn(user1);
        when(itemRepository.findAllByOwnerIdWithBookings(userId)).thenReturn(allItems);

        List<ItemForResponseDto> result = itemService.getAllItems(userId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(validationService).isExistUser(userId);
        verify(itemRepository).findAllByOwnerIdWithBookings(userId);
    }

    @Test
    void testGetAllItemsWithoutBookings() {
        Long userId = 1L;

        item.setId(1L);
        item.setOwner(user1);
        item.setBookings(new HashSet<>());

        item2.setId(2L);
        item2.setOwner(user1);
        item2.setBookings(new HashSet<>());

        when(validationService.isExistUser(userId)).thenReturn(user1);
        when(itemRepository.findAllByOwnerIdWithBookings(userId)).thenReturn(Collections.emptyList());

        List<ItemForResponseDto> result = itemService.getAllItems(userId);

        assertEquals(0, result.size());

        verify(validationService).isExistUser(userId);
        verify(itemRepository).findAllByOwnerIdWithBookings(userId);
    }

    @Test
    void testCreateItem() {
        Long ownerId = 1L;
        item.setId(null);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("name");
        savedItem.setDescription("description");
        savedItem.setOwner(user1);

        when(validationService.isExistUser(ownerId)).thenReturn(user1);
        when(itemRepository.save(item)).thenReturn(savedItem);

        ItemForResponseDto result = itemService.createItem(ownerId, createUpdateItemDto);

        assertEquals(1L, result.getId());
        assertEquals("name", result.getName());
        assertEquals("description", result.getDescription());

        verify(validationService).isExistUser(ownerId);
        verify(validationService).validateItemFields(item);
        verify(itemRepository).save(item);
    }

    @Test
    void testUpdateItem() {
        Long ownerId = 1L;
        createUpdateItemDto.setName("Updated Name");
        createUpdateItemDto.setDescription("Updated Description");
        createUpdateItemDto.setAvailable(false);

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Name");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(false);
        updatedItem.setOwner(user1);

        when(validationService.isExistUser(ownerId)).thenReturn(user1);
        when(validationService.isExistItem(1L)).thenReturn(item);
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        ItemForResponseDto result = itemService.updateItem(ownerId, 1L, createUpdateItemDto);

        assertEquals(1L, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());

        verify(validationService).isExistUser(ownerId);
        verify(validationService).isExistItem(1L);
        verify(itemRepository).save(updatedItem);
    }

    @Test
    void testUpdateItemWithInvalidOwner() {
        Long ownerId = 2L;
        Long itemId = 1L;
        item.setId(itemId);
        item.setOwner(user1);

        when(validationService.isExistUser(ownerId)).thenReturn(new User());
        when(validationService.isExistItem(itemId)).thenReturn(item);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(ownerId, itemId, new CreateUpdateItemDto()));

        verify(validationService).isExistUser(ownerId);
        verify(validationService).isExistItem(itemId);
        verifyNoInteractions(itemRepository);
    }

    @Test
    public void testGetItemByIdWithMatchingOwner() {
        Long ownerId = 1L;
        Long itemId = 1L;
        user1.setId(ownerId);
        item.setId(itemId);
        item.setOwner(user1);
        item.setBookings(Set.of(booking1));

        when(validationService.isExistUser(ownerId)).thenReturn(user1);
        when(validationService.isExistItem(itemId)).thenReturn(item);

        ItemForResponseDto result = itemService.getItemById(ownerId, itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    public void testGetItemByIdWithoutMatchingOwner() {
        Long ownerId = 1L;
        Long itemId = 2L;
        user1.setId(3L); // Different owner ID
        item.setId(itemId);
        item.setOwner(user1);
        item.setBookings(Set.of(booking1));

        when(validationService.isExistUser(ownerId)).thenReturn(user1);
        when(validationService.isExistItem(itemId)).thenReturn(item);

        ItemForResponseDto result = itemService.getItemById(ownerId, itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
    }

    @Test
    public void testDeleteItem() {
        Long ownerId = 1L;
        Long itemId = 2L;

        itemService.deleteItem(ownerId, itemId);

        verify(validationService).isExistUser(ownerId);
        verify(validationService).isExistItem(itemId);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    public void testSearchItemWithEmptyText() {
        Long ownerId = 1L;
        String text = "";

        List<ItemForResponseDto> result = itemService.searchItem(ownerId, text);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchItemWithNonEmptyText() {
        Long ownerId = 1L;
        String text = "search";
        item.setDescription("search");
        item2.setDescription("search");
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);

        when(itemRepository.searchItems(text)).thenReturn(items);

        List<ItemForResponseDto> result = itemService.searchItem(ownerId, text);

        assertNotNull(result);
        assertEquals(items.size(), result.size());
        verify(itemRepository).searchItems(text);
    }

    @Test
    public void testCreateCommentWithBlankText() {
        Long bookerId = 1L;
        Long itemId = 2L;
        CreateCommentDto commentDto = new CreateCommentDto();
        commentDto.setText("");

        assertThrows(ValidationException.class, () -> {
            itemService.createComment(bookerId, itemId, commentDto);
        });
    }

    @Test
    public void testCreateCommentWithValidTextAndBooking() {
        Long bookerId = 1L;
        Long itemId = 2L;
        CreateCommentDto commentDto = new CreateCommentDto();
        commentDto.setText("Great item!");

        Comment comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(item)
                .author(user1)
                .build();

        when(validationService.isExistUser(bookerId)).thenReturn(user1);
        when(validationService.isExistItem(itemId)).thenReturn(item);
        when(validationService.isBookingByUser(user1, item)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(bookerId, itemId, commentDto);

        assertNotNull(result);
        verify(validationService).isExistUser(bookerId);
        verify(validationService).isExistItem(itemId);
        verify(validationService).isBookingByUser(user1, item);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    public void testCreateCommentWithValidTextButNoBooking() {
        Long bookerId = 2L;
        Long itemId = 1L;
        CreateCommentDto commentDto = new CreateCommentDto();
        commentDto.setText("Great item!");

        when(validationService.isExistUser(bookerId)).thenReturn(user1);
        when(validationService.isExistItem(itemId)).thenReturn(item);
        when(validationService.isBookingByUser(user1, item)).thenReturn(false);

        assertThrows(NotAvailableException.class, () -> {
            itemService.createComment(bookerId, itemId, commentDto);
        });
    }
}
