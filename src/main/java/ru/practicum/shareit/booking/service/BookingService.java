package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {
    BookingForResponse createBooking(Long bookerId, CreateBookingDto createBookingDto);

    BookingForResponse updateBooking(Long ownerId, Long bookingId, Boolean approved);

    List<BookingForResponse> getAllUserBookings(Long userId, String state, Integer from, Integer size);

    List<BookingForResponse> getAllOwnerBookings(Long ownerId, String state, Integer from, Integer size);

    BookingForResponse getBookingByOwner(Long userId, Long bookingId);
}
