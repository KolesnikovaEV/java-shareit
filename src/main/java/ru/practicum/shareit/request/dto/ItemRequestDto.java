package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotNull(groups = {CreateObject.class})
    @NotBlank(groups = {CreateObject.class})
    private String description;
    //    private UserForResponseDto requester;
    private LocalDateTime created;
    private List<ItemForResponseDto> items;
    private Long requestId;
}
