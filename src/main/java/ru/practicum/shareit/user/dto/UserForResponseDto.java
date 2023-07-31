package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class UserForResponseDto {
    private Long id;
    private String name;
    private String email;
}
