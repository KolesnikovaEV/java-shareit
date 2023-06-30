package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllItems(Long userId);

    Item createItem(Long ownerId, Item item);

    Item updateItem(Item item, boolean[] isUpdateField);

    Item getItemById(Long itemId);

    void deleteItem(Long itemId);

    List<Item> searchItems(String text);
}
