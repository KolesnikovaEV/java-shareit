package ru.practicum.gate.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserForResponseDto {
    private Long id;
    private String name;
    private String email;
}
