package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    Long id;
    String description;
    Long requester;   //пользователь, создавший запрос
    LocalDateTime created;
}
