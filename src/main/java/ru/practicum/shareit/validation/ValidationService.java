package ru.practicum.shareit.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.util.Set;

@Service
@Slf4j
public class ValidationService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ValidationService(ItemRepository itemRepository, UserRepository userRepository,
                             BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }


    public User isExistUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found in DB.")
        );
    }

    public Booking isExistBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking is not found.")
        );
    }

    public Item isExistItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item is not found"));
    }

    public void validateUserFields(User user) {
        log.info("Validation is going.");
        final String email = user.getEmail();
        if (email == null || email.isBlank() || !email.contains("@")) {
            String error = "Email cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
        final String name = user.getName();
        if (name == null || name.isBlank()) {
            String error = "Name cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
    }

    public void validateBooking(CreateBookingDto createBookingDto, Item item, User booker) {
        if (createBookingDto.getStart() == null || createBookingDto.getEnd() == null) {
            log.info("Start booking or end can not be null");
            throw new NotValidDateException("Start booking or end can not be null");
        }
        if (createBookingDto.getEnd().isBefore(createBookingDto.getStart()) ||
                createBookingDto.getEnd().isEqual(createBookingDto.getStart())) {
            log.info("End of booking can not be earlier or equals start of booking");
            throw new NotValidDateException("End of booking can not be earlier or equals start of booking");
        }
        if (item.getOwner().equals(booker)) {
            log.info("Owner can not book own Item");
            throw new NotFoundException("Owner can not book own Item");
        }

        if (createBookingDto.getStart().isBefore(LocalDateTime.now()) ||
                createBookingDto.getEnd().isBefore(LocalDateTime.now())) {
            log.info("Start or end of booking can not be in the past");
            throw new NotValidDateException("Start or end of booking can not be in the past");
        }

        Set<Booking> bookings = item.getBookings();
        if (!bookings.isEmpty()) {
            for (Booking b : bookings) {
                if (!(b.getEnd().isBefore(createBookingDto.getStart()) ||
                        b.getStart().isAfter(createBookingDto.getStart()))) {
                    log.info("Item can not be booked");
                    throw new NotAvailableException("Item can not be booked");
                }
            }
        }
    }

    public void validateItemFields(Item item) {
        final String name = item.getName();
        final String description = item.getDescription();
        if (name == null || name.isBlank()) {
            String error = "Item name cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
        if (description == null || description.isBlank()) {
            String error = "Item description cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
        final Boolean available = item.getAvailable();
        if (available == null) {
            String error = "Item availability cannot be empty.";
            log.info(error);
            throw new ValidationException(error);
        }
    }

    public Boolean isBookingByUser(User user, Item item) {
        LocalDateTime currentTime = LocalDateTime.now();
        return item.getBookings()
                .stream()
                .anyMatch(t -> t.getBooker().equals(user) && t.getEnd().isBefore(currentTime));
    }
}
