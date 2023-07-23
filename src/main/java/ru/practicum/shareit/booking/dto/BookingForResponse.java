package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.dto.UserOnlyWithIdDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingForResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private UserOnlyWithIdDto booker;
    private ItemWithBookingDto item;

}
