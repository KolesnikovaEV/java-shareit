package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingForResponse createBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long bookerId,
                                            @Valid @Validated(CreateObject.class) @RequestBody CreateBookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    BookingForResponse updateBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingForResponse getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBookingByOwner(userId, bookingId);
    }

    @GetMapping
    List<BookingForResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state",
                                                        defaultValue = "ALL", required = false) String state) {
        return bookingService.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingForResponse> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(value = "state", defaultValue = "ALL",
                                                                required = false) String state) {
        return bookingService.getAllOwnerBookings(userId, state);
    }
}
