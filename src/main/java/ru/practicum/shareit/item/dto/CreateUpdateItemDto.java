package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUpdateItemDto {
    @NotBlank(groups = CreateObject.class)
    private String name;

    @NotBlank(groups = CreateObject.class)
    private String description;

    @NotNull(groups = CreateObject.class)
    private Boolean available;

    private Long requestId;
}
