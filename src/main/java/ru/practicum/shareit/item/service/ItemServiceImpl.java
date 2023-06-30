package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.validation.ValidationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ValidationService validationService;
    private final ItemMapper mapper;

    public ItemServiceImpl(@Qualifier("InMemory") ItemRepository itemRepository, ItemMapper mapper,
                           ValidationService validationService) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.validationService = validationService;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<ItemDto> allItemsDto = new ArrayList<>();
        List<Item> allItems = itemRepository.getAllItems(userId);
        allItems.stream().map(mapper::toItemDto).forEach(allItemsDto::add);
        log.info("All items are shown");
        return allItemsDto;
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto item) {
        log.info("Creating new item");
        item.setOwner(ownerId);
        Item newItem = mapper.toItem(item);
        validationService.validateItemFields(newItem);
        validationService.isExistUser(ownerId);
        ItemDto createdItem = mapper.toItemDto(itemRepository.createItem(ownerId, newItem));
        log.info("New item {} created", createdItem.getId());
        return createdItem;
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto item) {
        if (ownerId == null) {
            String message = "Owner ID needed";
            log.info(message);
            throw new ValidationException(message);
        }
        item.setOwner(ownerId);
        item.setId(itemId);
        Item mapperItem = mapper.toItem(item);
        Item existItem = validationService.isExistItem(mapperItem.getId());

        if (!validationService.isOwnerItem(existItem, ownerId)) {
            String message = String.format("Item %s doesn't belong to User ID = %d.", existItem.getName(), ownerId);
            throw new NotFoundException(message);
        }
        validationService.isExistUser(ownerId);
        boolean[] isUpdateFields = validationService.checkFieldsForUpdateItem(mapperItem);
        ItemDto result = mapper.toItemDto(itemRepository.updateItem(mapperItem, isUpdateFields));
        log.info("Item updated {}, id = {}", result.getName(), result.getId());
        return result;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        validationService.isExistItem(itemId);
        Item getItem = itemRepository.getItemById(itemId);
        log.info("Item shown");
        return mapper.toItemDto(getItem);
    }

    @Override
    public void deleteItem(long itemId) {

    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            String message = String.format("Text is empty");
            log.info(message);
            return Collections.emptyList();
        }

        List<ItemDto> list = new ArrayList<>();
        for (Item item : itemRepository.searchItems(text)) {
            ItemDto itemDto = mapper.toItemDto(item);
            list.add(itemDto);
        }
        String message = String.format("Items are found '%s' by text: %s", list, text);
        log.info(message);

        return list;
    }
}
