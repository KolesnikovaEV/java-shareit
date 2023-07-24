package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class CreateUpdateItemDto {
    @NotBlank(groups = CreateObject.class)
    private String name;

    @NotBlank(groups = CreateObject.class)
    private String description;

    @NotNull(groups = CreateObject.class)
    private Boolean available;
}
