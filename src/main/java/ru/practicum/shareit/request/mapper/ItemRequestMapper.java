package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public ItemRequestForResponse toItemRequestForResponse(ItemRequest itemRequest) {
        ItemRequestForResponse requestDto = new ItemRequestForResponse();

        requestDto.setId(itemRequest.getId());
        requestDto.setDescription(itemRequest.getDescription());
        requestDto.setCreated(itemRequest.getCreated());
        requestDto.setRequester(UserMapper.userForResponseDto(itemRequest.getRequester()));

        if (itemRequest.getItems() != null) {
            requestDto.setItems(itemRequest.getItems().stream()
                    .map(ItemMapper::toGetItemDtoFromItem).collect(Collectors.toList()));
        }

        return requestDto;
    }
}
