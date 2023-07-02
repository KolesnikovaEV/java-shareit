package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Qualifier("InMemory")
@Slf4j
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> itemRepository = new HashMap<>();
    private Long generateId = 1L;

    @Override
    public List<Item> getAllItems(Long userId) {
        return itemRepository.values().stream().filter(i -> i.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Long ownerId, Item item) {
        item.setId(generateId++);
        itemRepository.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, List<Boolean> isUpdateField) {
        final Long inputId = item.getId();
        final String inputName = item.getName();
        final String inputDesc = item.getDescription();
        final Boolean inputAvailable = item.getAvailable();

        Item toUpdate = itemRepository.get(inputId);

        if (isUpdateField.get(0)) {
            toUpdate.setName(inputName);
        }
        if (isUpdateField.get(1)) {
            toUpdate.setDescription(inputDesc);
        }
        if (isUpdateField.get(2)) {
            toUpdate.setAvailable(inputAvailable);
        }
        itemRepository.put(inputId, toUpdate);
        return toUpdate;
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.get(itemId);
    }


    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .collect(Collectors.toList());
    }
}
