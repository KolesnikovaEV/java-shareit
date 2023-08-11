package ru.practicum.gate.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gate.booking.dto.CreateBookingDto;
import ru.practicum.gate.constants.Constants;
import ru.practicum.gate.exception.ValidationException;
import ru.practicum.gate.validation.CreateObject;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(value = Constants.USER_ID_HEADER) Long bookerId,
                                                @RequestBody @Validated(CreateObject.class)
                                                @Valid CreateBookingDto bookingDto) {
        log.info("Creating booking");
        //checkStartAndEndTimes(bookingDto);
        return bookingClient.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Updating booking");
        return bookingClient.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByOwner(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                    @PathVariable Long bookingId) {
        log.info("Getting Booking by Owner");
        return bookingClient.getBookingByOwner(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                     @RequestParam(value = "state",
                                                             defaultValue = "ALL") String state,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Getting Booking by User");
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                      @RequestParam(value = "state", defaultValue = "ALL")
                                                      String state,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Getting All Bookings by Owner");
        return bookingClient.getAllOwnerBookings(userId, state, from, size);
    }

    private void checkStartAndEndTimes(CreateBookingDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            String message = "Окончание бронирования не может быть раньше его начала.";
            log.info(message);
            throw new ru.practicum.gate.exception.ValidationException(message);
        }
        if (start == null || end == null) {
            throw new ru.practicum.gate.exception.ValidationException("Окончание бронирования не может быть раньше его начала.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ru.practicum.gate.exception.ValidationException("Окончание бронирования не может быть раньше его начала.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Окончание бронирования не может быть раньше его начала.");
        }
    }
}
