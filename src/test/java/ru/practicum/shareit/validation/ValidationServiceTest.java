package ru.practicum.shareit.validation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidDateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ValidationService validationService;

    private User user1, user2;
    private Item item;
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
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@ya.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@ya.ru").build();

        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user1)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(user2)
                .build();

        booking = Booking.builder()
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
                .booker(user2)
                .status(WAITING)
                .build();
        createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

    }

    @Test
    public void testIsExistUser_ExistingUser_ReturnsUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        User result = validationService.isExistUser(userId);

        assertEquals(user1, result);
    }

    @Test
    public void testIsExistBooking_ExistingBooking_ReturnsBooking() {
        Long bookingId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = validationService.isExistBooking(bookingId);

        assertEquals(booking, result);
    }

    @Test
    public void testIsExistItem_ExistingItem_ReturnsItem() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = validationService.isExistItem(itemId);

        assertEquals(item, result);
    }

    @Test
    public void testIsExistUser_WhenUserNotFound_ThrowsNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> validationService.isExistUser(userId));
    }

    @Test
    public void testValidateUserFields_ValidUser_NoExceptionThrown() {
        validationService.validateUserFields(user1);
    }

    @Test
    public void testIsExistBooking_WhenBookingNotFound_ThrowsNotFoundException() {
        Long bookingId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> validationService.isExistBooking(bookingId));
    }

    @Test
    public void testIsExistItem_WhenItemNotFound_ThrowsNotFoundException() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> validationService.isExistItem(itemId));
    }

    @Test
    public void testValidateUserFields_EmailIsNull_ThrowsValidationException() {
        User user = new User();
        user.setName("John");

        assertThrows(ValidationException.class, () -> validationService.validateUserFields(user));
    }

    @Test
    public void testValidateUserFields_EmailIsEmpty_ThrowsValidationException() {
        User user = new User();
        user.setName("John");
        user.setEmail("");

        assertThrows(ValidationException.class, () -> validationService.validateUserFields(user));
    }

    @Test
    public void testValidateUserFields_EmailDoesNotContainAtSymbol_ThrowsValidationException() {
        User user = new User();
        user.setName("John");
        user.setEmail("john.doe.example.com");

        assertThrows(ValidationException.class, () -> validationService.validateUserFields(user));
    }

    @Test
    public void testValidateUserFields_NameIsNull_ThrowsValidationException() {
        User user = new User();
        user.setEmail("john.doe@example.com");

        assertThrows(ValidationException.class, () -> validationService.validateUserFields(user));
    }

    @Test
    public void testValidateUserFields_NameIsEmpty_ThrowsValidationException() {
        User user = new User();
        user.setName("");
        user.setEmail("john.doe@example.com");

        assertThrows(ValidationException.class, () -> validationService.validateUserFields(user));
    }

    @Test
    public void testValidateBooking_StartAndEndNull_ThrowsNotValidDateException() {
        createBookingDto.setEnd(null);
        createBookingDto.setStart(null);

        assertThrows(NotValidDateException.class, () -> validationService.validateBooking(createBookingDto, item, user1));
    }

    @Test
    public void testValidateBooking_EndBeforeStart_ThrowsNotValidDateException() {
        createBookingDto.setStart(LocalDateTime.now());
        createBookingDto.setEnd(LocalDateTime.now().minusHours(1));

        assertThrows(NotValidDateException.class, () -> validationService.validateBooking(createBookingDto, item, user1));
    }

    @Test
    public void testValidateBooking_OwnerBookingOwnItem_ThrowsNotFoundException() {
        createBookingDto.setStart(LocalDateTime.now());
        createBookingDto.setEnd(LocalDateTime.now().plusHours(1));

        item.setOwner(user1);

        assertThrows(NotFoundException.class, () -> validationService.validateBooking(createBookingDto, item, user1));
    }

    @Test
    public void testValidateBooking_StartAndEndInPast_ThrowsNotValidDateException() {
        createBookingDto.setStart(LocalDateTime.now().minusHours(1));
        createBookingDto.setEnd(LocalDateTime.now().minusMinutes(30));

        assertThrows(NotValidDateException.class, () -> validationService.validateBooking(createBookingDto, item, user2));
    }

    @Test
    public void testValidateBooking_BookingsExistWithinBookingPeriod_ThrowsNotAvailableException() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        booking.setStart(start.minusHours(1));
        booking.setEnd(start.plusMinutes(30));

        booking2.setStart(start.plusMinutes(45));
        booking2.setEnd(end.plusMinutes(30));

        Set<Booking> bookings = new HashSet<>();
        bookings.add(booking);
        bookings.add(booking2);

        createBookingDto.setStart(start);
        createBookingDto.setEnd(end);
        item.setBookings(bookings);
        User booker = new User();

        assertThrows(NotAvailableException.class, () -> validationService.validateBooking(createBookingDto, item, booker));
    }

    @Test
    public void testValidateItemFields_NameIsNull_ThrowsValidationException() {
        item.setName(null);

        assertThrows(ValidationException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    public void testValidateItemFields_NameIsEmpty_ThrowsValidationException() {
        item.setName("");

        assertThrows(ValidationException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    public void testValidateItemFields_DescriptionIsNull_ThrowsValidationException() {
        item.setDescription(null);

        assertThrows(ValidationException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    public void testValidateItemFields_DescriptionIsEmpty_ThrowsValidationException() {
        item.setName("Test Name");
        item.setDescription("");
        item.setAvailable(true);

        assertThrows(ValidationException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    public void testValidateItemFields_AvailableIsNull_ThrowsValidationException() {
        item.setAvailable(null);

        assertThrows(ValidationException.class, () -> validationService.validateItemFields(item));
    }

    @Test
    public void testIsBookingByUser_BookingsExist_ReturnsTrue() {
        booking.setBooker(user1);
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking2.setBooker(user1);
        booking2.setEnd(LocalDateTime.now().minusHours(2));
        item.setBookings(Set.of(booking, booking2));

        boolean result = validationService.isBookingByUser(user1, item);

        assertTrue(result);
    }

    @Test
    public void testIsBookingByUser_NoBookingsExist_ReturnsFalse() {
        item.setBookings(new HashSet<>());
        boolean result = validationService.isBookingByUser(user2, item);

        assertFalse(result);
    }

    @Test
    public void testIsBookingByUser_BookingsExistButNotByUser_ReturnsFalse() {
        booking.setBooker(user1);
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking2.setBooker(user1);
        booking2.setEnd(LocalDateTime.now().minusHours(2));

        item.setBookings(Set.of(booking, booking2));

        boolean result = validationService.isBookingByUser(user2, item);

        assertFalse(result);
    }


}
