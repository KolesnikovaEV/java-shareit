package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constant.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public BookingForResponse createBooking(@RequestHeader(value = USER_ID_HEADER) Long bookerId,
                                            @Valid @Validated(CreateObject.class) @RequestBody CreateBookingDto bookingDto) {
        log.info("Creating booking");
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    BookingForResponse updateBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        log.info("Updating booking");
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingForResponse getBookingByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Getting Booking by Owner");
        return bookingService.getBookingByOwner(userId, bookingId);
    }

    @GetMapping
    List<BookingForResponse> getAllUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @RequestParam(value = "state",
                                                        defaultValue = "ALL") String state,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Getting Booking by User");
        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingForResponse> getAllOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @RequestParam(value = "state", defaultValue = "ALL")
                                                        String state,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Getting All Bookings by Owner");
        return bookingService.getAllOwnerBookings(userId, state, from, size);
    }
}
