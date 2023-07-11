package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long item;
    Long booker;
    Status status;
}
