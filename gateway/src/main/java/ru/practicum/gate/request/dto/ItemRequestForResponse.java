package ru.practicum.gate.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gate.item.dto.ItemForResponseDto;
import ru.practicum.gate.user.dto.UserForResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestForResponse {
    private Long id;
    private String description;
    private UserForResponseDto requester;
    private LocalDateTime created;
    private List<ItemForResponseDto> items;
}
