package ru.practicum.gate.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.gate.item.dto.ItemWithBookingDto;
import ru.practicum.gate.user.dto.UserOnlyWithIdDto;
import ru.practicum.gate.booking.model.Status;

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
