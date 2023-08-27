package ru.practicum.gate.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ItemDtoForItemRequest {
    private Long id;
    private String name;
    private Long requestId;
}
