package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.Status.REJECTED;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ValidationService validationService;

    private Item item;
    private User user;
    private Booking booking, booking2;
    private CreateBookingDto createBookingDto;
    private static LocalDateTime start;
    private static LocalDateTime end;

    @BeforeAll
    static void beforeAll() {
        start = LocalDateTime.now();
        end = LocalDateTime.now();
    }

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
        Item item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(WAITING)
                .build();
        booking2 = Booking.builder()
                .id(2L)
                .start(start)
                .end(end)
                .item(item2)
                .booker(user)
                .status(WAITING)
                .build();

        createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

    }

    @Test
    public void testCreateBooking() {
        when(bookingRepository.save(any())).thenReturn(booking);
        when(validationService.isExistUser(anyLong())).thenReturn(user);
        when(validationService.isExistItem(anyLong())).thenReturn(item);

        BookingForResponse savedBookingForResponse = bookingService.createBooking(user.getId(), createBookingDto);

        Assertions.assertNotNull(savedBookingForResponse);
        assertEquals(createBookingDto.getStart(), savedBookingForResponse.getStart());
        assertEquals(createBookingDto.getEnd(), savedBookingForResponse.getEnd());
        assertEquals(createBookingDto.getItemId(), savedBookingForResponse.getItem().getId());
    }

    @Test
    public void testUpdateBooking() {
        when(validationService.isExistUser(anyLong())).thenReturn(user);
        when(validationService.isExistBooking(anyLong())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingForResponse updatedBookingForResponse = bookingService
                .updateBooking(user.getId(), booking.getId(), true);

        Assertions.assertNotNull(updatedBookingForResponse);
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    public void testCreateBooking_InvalidUser() {
        Long userId = 1L;
        when(validationService.isExistUser(userId)).thenThrow(new NotFoundException("User not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(userId, createBookingDto);
        });
    }

    @Test
    public void testCreateBooking_InvalidItem() {
        Long userId = 1L;
        Long itemId = 2L;

        createBookingDto.setItemId(itemId);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(validationService.isExistItem(itemId)).thenThrow(new NotFoundException("Item not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(userId, createBookingDto);
        });
    }

    @Test
    public void testCreateBooking_UnavailableItem() {
        Long userId = 1L;
        Long itemId = 2L;

        createBookingDto.setItemId(itemId);
        item.setAvailable(false);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(validationService.isExistItem(itemId)).thenReturn(item);

        Assertions.assertThrows(NotAvailableException.class, () -> {
            bookingService.createBooking(userId, createBookingDto);
        });
    }

    @Test
    public void testUpdateBooking_InvalidUser() {
        Long userId = 1L;
        Long bookingId = 2L;

        when(validationService.isExistUser(userId)).thenThrow(new NotFoundException("User not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(userId, bookingId, true);
        });
    }

    @Test
    public void testUpdateBooking_InvalidBooking() {
        Long userId = 1L;
        Long bookingId = 2L;

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(validationService.isExistBooking(bookingId)).thenThrow(new NotFoundException("Booking not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(userId, bookingId, true);
        });
    }

    @Test
    public void testUpdateBooking_WasApprovedBooking() {
        Long userId = user.getId();
        Long bookingId = booking.getId();
        booking.setStatus(Status.APPROVED);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(validationService.isExistBooking(bookingId)).thenReturn(booking);

        Assertions.assertThrows(NotAvailableException.class, () -> {
            bookingService.updateBooking(userId, bookingId, true);
        });
    }

    @Test
    public void testUpdateBooking_RejectBooking() {
        when(validationService.isExistUser(anyLong())).thenReturn(user);
        when(validationService.isExistBooking(anyLong())).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingForResponse updatedBookingForResponse = bookingService
                .updateBooking(user.getId(), booking.getId(), false);

        Assertions.assertNotNull(updatedBookingForResponse);
        assertEquals(REJECTED, booking.getStatus());
    }

    @Test
    public void testGetBookingByOwner_ValidOwner() {
        Long userId = 1L;
        Long bookingId = 2L;

        user.setId(userId);

        booking.setId(bookingId);
        booking.getItem().setOwner(user);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(validationService.isExistBooking(bookingId)).thenReturn(booking);

        BookingForResponse bookingForResponse = bookingService.getBookingByOwner(userId, bookingId);

        assertNotNull(bookingForResponse);
        assertEquals(bookingId, bookingForResponse.getId());
    }

    @Test
    public void testGetBookingByOwner_ValidBooker() {
        Long userId = 1L;
        Long bookingId = 2L;

        user.setId(userId);

        booking.setId(bookingId);
        booking.setBooker(user);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(validationService.isExistBooking(bookingId)).thenReturn(booking);

        BookingForResponse bookingForResponse = bookingService.getBookingByOwner(userId, bookingId);

        assertNotNull(bookingForResponse);
        assertEquals(bookingId, bookingForResponse.getId());
    }

    @Test
    public void testGetBookingByOwner_InvalidUser() {
        Long userId = 1L;
        Long bookingId = 2L;

        when(validationService.isExistUser(userId)).thenThrow(new NotFoundException("User not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingByOwner(userId, bookingId);
        });
    }

    @Test
    public void testGetBookingByOwner_InvalidBooking() {
        Long userId = 1L;
        Long bookingId = 2L;

        when(validationService.isExistUser(userId)).thenReturn(new User());
        when(validationService.isExistBooking(bookingId)).thenThrow(new NotFoundException("Booking not found"));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingByOwner(userId, bookingId);
        });
    }

    @Test
    public void testGetBookingByOwner_InvalidUserOrOwner() {
        Long userId = 1L;
        Long bookingId = 2L;

        user.setId(3L);

        booking.setId(bookingId);
        booking.getItem().setOwner(user);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(validationService.isExistBooking(bookingId)).thenReturn(booking);

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingByOwner(userId, bookingId);
        });
    }

    @Test
    public void testGetAllUserBookings_ValidState() {
        Long userId = 1L;
        String state = "CURRENT";
        Integer from = 0;
        Integer size = 10;

        user.setId(userId);

        LocalDateTime nowDateTime = LocalDateTime.now();

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingForResponse> bookingForResponses = bookingService.getAllUserBookings(userId, state, from, size);

        assertNotNull(bookingForResponses);
        assertEquals(1, bookingForResponses.size());
    }

    @Test
    public void testGetAllUserBookings_InvalidState() {
        Long userId = 1L;
        String state = "UNSUPPORTED_STATUS";
        Integer from = 0;
        Integer size = 10;

        when(validationService.isExistUser(userId)).thenReturn(user);

        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.getAllUserBookings(userId, state, from, size);
        });
    }

    @Test
    public void testGetAllUserBookings_StateAll() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        user.setId(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(bookingRepository.findAllByBookerOrderByStartDesc(user, pageable)).thenReturn(bookings);

        List<BookingForResponse> bookingForResponses = bookingService.getAllUserBookings(userId, state, from, size);

        verify(validationService).isExistUser(userId);

        verify(bookingRepository).findAllByBookerOrderByStartDesc(user, pageable);

        assertEquals(bookings.size(), bookingForResponses.size());
    }

    @Test
    public void testGetAllUserBookings_FutureState() {
        Long userId = 1L;
        String state = "FUTURE";
        int from = 0;
        int size = 10;
        LocalDateTime nowDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));


        when(validationService.isExistUser(userId)).thenReturn(user);

        booking.setId(1L);
        booking.setStart(nowDateTime.plusDays(10));
        booking.setEnd(nowDateTime.plusDays(15));
        booking.setStatus(WAITING);

        booking2.setId(2L);
        booking2.setStart(nowDateTime.plusDays(9));
        booking2.setEnd(nowDateTime.plusDays(16));
        booking2.setStatus(REJECTED);

        List<Booking> bookings = Arrays.asList(booking, booking2);


        when(bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(
                eq(user), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookings);

        List<BookingForResponse> result = bookingService.getAllUserBookings(userId, state, from, size);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testGetAllUserBookings_PastState() {
        Long userId = 1L;
        String state = "PAST";
        int from = 0;
        int size = 10;
        LocalDateTime nowDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));


        when(validationService.isExistUser(userId)).thenReturn(user);

        booking.setId(1L);
        booking.setStart(nowDateTime.minusDays(20));
        booking.setEnd(nowDateTime.minusDays(15));

        booking2.setId(2L);
        booking2.setStart(nowDateTime.minusDays(20));
        booking2.setEnd(nowDateTime.minusDays(16));

        List<Booking> bookings = Arrays.asList(booking, booking2);


        when(bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(
                eq(user), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookings);

        List<BookingForResponse> result = bookingService.getAllUserBookings(userId, state, from, size);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testGetWaiting_Bookings() {
        Long userId = 1L;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 10;
        user.setId(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));

        List<Booking> bookings = new ArrayList<>();
        booking.setStatus(WAITING);
        booking2.setStatus(WAITING);
        bookings.add(booking);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user,
                Status.WAITING, pageable)).thenReturn(bookings);

        List<BookingForResponse> bookingForResponses = bookingService.getAllUserBookings(userId, state, from, size);

        verify(validationService).isExistUser(userId);

        verify(bookingRepository).findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.WAITING, pageable);

        assertEquals(bookings.size(), bookingForResponses.size());
    }

    @Test
    public void testGetRejected_Bookings() {
        Long userId = 1L;
        String state = "REJECTED";
        Integer from = 0;
        Integer size = 10;
        user.setId(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));
        LocalDateTime nowDateTime = LocalDateTime.now();

        List<Booking> bookings = new ArrayList<>();
        booking.setStatus(REJECTED);
        booking2.setStatus(REJECTED);
        bookings.add(booking);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(user,
                Status.REJECTED, pageable)).thenReturn(bookings);

        List<BookingForResponse> bookingForResponses = bookingService.getAllUserBookings(userId, state, from, size);

        verify(validationService).isExistUser(userId);

        verify(bookingRepository).findAllByBookerAndStatusEqualsOrderByStartDesc(user, Status.REJECTED, pageable);

        assertEquals(bookings.size(), bookingForResponses.size());
    }

    @Test
    public void testGetAllOwnerBookings_AllState() {
        Long userId = 1L;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;
        LocalDateTime nowDateTime = LocalDateTime.now();

        when(validationService.isExistUser(userId)).thenReturn(user);

        List<Booking> bookings = Arrays.asList(
                booking, booking2
        );
        when(bookingRepository.findAllByItem_OwnerOrderByStartDesc(user, PageRequest.of(0, 10,
                Sort.by("start"))))
                .thenReturn(bookings);

        List<BookingForResponse> result = bookingService.getAllOwnerBookings(userId, state, from, size);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testGetAllOwnerBookings_CurrentState() {
        Long userId = 1L;
        String state = "CURRENT";
        int from = 0;
        int size = 10;
        LocalDateTime nowDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));


        when(validationService.isExistUser(userId)).thenReturn(user);

        booking.setId(1L);
        booking.setStatus(WAITING);

        booking2.setId(2L);
        booking2.setStatus(REJECTED);

        List<Booking> bookings = Arrays.asList(booking, booking2);


        when(bookingRepository.findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(user), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookings);

        List<BookingForResponse> result = bookingService.getAllOwnerBookings(userId, state, from, size);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testGetAllOwnerBookings_PastState() {
        Long userId = 1L;
        String state = "PAST";
        int from = 0;
        int size = 10;
        LocalDateTime nowDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));


        when(validationService.isExistUser(userId)).thenReturn(user);

        booking.setId(1L);
        booking.setStart(nowDateTime.minusDays(10));
        booking.setEnd(nowDateTime.minusDays(8));
        booking.setStatus(WAITING);

        booking2.setId(2L);
        booking2.setStart(nowDateTime.minusDays(9));
        booking2.setEnd(nowDateTime.minusDays(5));
        booking2.setStatus(REJECTED);

        List<Booking> bookings = Arrays.asList(booking, booking2);


        when(bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
                eq(user), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookings);

        List<BookingForResponse> result = bookingService.getAllOwnerBookings(userId, state, from, size);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testGetAllOwnerBookings_FutureState() {
        Long userId = 1L;
        String state = "FUTURE";
        int from = 0;
        int size = 10;
        LocalDateTime nowDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));


        when(validationService.isExistUser(userId)).thenReturn(user);

        booking.setId(1L);
        booking.setStart(nowDateTime.plusDays(10));
        booking.setEnd(nowDateTime.plusDays(15));
        booking.setStatus(WAITING);

        booking2.setId(2L);
        booking2.setStart(nowDateTime.plusDays(9));
        booking2.setEnd(nowDateTime.plusDays(16));
        booking2.setStatus(REJECTED);

        List<Booking> bookings = Arrays.asList(booking, booking2);


        when(bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(
                eq(user), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookings);

        List<BookingForResponse> result = bookingService.getAllOwnerBookings(userId, state, from, size);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void testGetWaitingOwner_Bookings() {
        Long userId = 1L;
        String state = "WAITING";
        Integer from = 0;
        Integer size = 10;
        user.setId(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));

        List<Booking> bookings = new ArrayList<>();
        booking.setStatus(WAITING);
        booking2.setStatus(WAITING);
        bookings.add(booking);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user,
                Status.WAITING, pageable)).thenReturn(bookings);

        List<BookingForResponse> bookingForResponses = bookingService.getAllOwnerBookings(userId, state, from, size);

        verify(validationService).isExistUser(userId);

        verify(bookingRepository).findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user, Status.WAITING, pageable);

        assertEquals(bookings.size(), bookingForResponses.size());
    }

    @Test
    public void testGetRejectedOwner_Bookings() {
        Long userId = 1L;
        String state = "REJECTED";
        Integer from = 0;
        Integer size = 10;
        user.setId(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));

        List<Booking> bookings = new ArrayList<>();
        booking.setStatus(REJECTED);
        booking2.setStatus(REJECTED);
        bookings.add(booking);

        when(validationService.isExistUser(userId)).thenReturn(user);
        when(bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user,
                Status.REJECTED, pageable)).thenReturn(bookings);

        List<BookingForResponse> bookingForResponses = bookingService.getAllOwnerBookings(userId, state, from, size);

        verify(validationService).isExistUser(userId);

        verify(bookingRepository).findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(user, Status.REJECTED, pageable);

        assertEquals(bookings.size(), bookingForResponses.size());
    }

    @Test
    public void testGetAllOwnerBookings_InvalidState() {
        Long userId = 1L;
        String state = "UNSUPPORTED_STATUS";
        Integer from = 0;
        Integer size = 10;

        when(validationService.isExistUser(userId)).thenReturn(user);

        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.getAllOwnerBookings(userId, state, from, size);
        });
    }

}
