package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor

public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Boolean request;
    private Set<Long> reviews;  //список с id отзывов
}
