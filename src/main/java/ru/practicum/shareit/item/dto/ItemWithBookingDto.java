package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemWithBookingDto {
    private Long id;
    private String name;
}
