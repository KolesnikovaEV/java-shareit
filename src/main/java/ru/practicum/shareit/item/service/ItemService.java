package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Long userId);

    ItemDto createItem(Long ownerId, ItemDto item);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto item);  // проверить этот метод

    ItemDto getItemById(Long itemId);

    void deleteItem(long itemId);

    List<ItemDto> searchItem(String str);

}
