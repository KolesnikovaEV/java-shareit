package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateUpdateItemDto {
    @NotBlank(groups = CreateObject.class)
    private String name;

    @NotBlank(groups = CreateObject.class)
    private String description;

    @NotNull(groups = CreateObject.class)
    private Boolean available;
}
