package ru.practicum.gate.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.gate.booking.dto.BookingForItemDto;

import java.util.List;

@Data
@Builder
public class ItemForResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
