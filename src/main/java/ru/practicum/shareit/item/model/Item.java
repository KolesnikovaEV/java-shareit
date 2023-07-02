package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data

public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;      //id пользователя
    @JsonIgnore
    private Boolean request;
    private Set<Long> reviews;  //список с id отзывов
}
