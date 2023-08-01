package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ValidationService validationService;


    @Override
    @Transactional(readOnly = true)
    public List<ItemForResponseDto> getAllItems(Long userId) {
        validationService.isExistUser(userId);
        List<Item> allItems = itemRepository.findAllByOwnerIdWithBookings(userId);
        log.info("All items are shown");
        if (!allItems.isEmpty() && Objects.equals(allItems.get(0).getOwner().getId(), userId)) {
            return allItems.stream()
                    .map(ItemMapper::toItemWIthBookingDto)
                    .collect(Collectors.toList());
        } else {
            return allItems.stream()
                    .map(ItemMapper::toGetItemDtoFromItem)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public ItemForResponseDto createItem(Long ownerId, CreateUpdateItemDto item) {
        log.info("Creating new item");
        validationService.validateItemFields(ItemMapper.toGetItemFromCreateUpdateItemDto(item));
        User owner = validationService.isExistUser(ownerId);

        Item newItem = ItemMapper.toGetItemFromCreateUpdateItemDto(item);
        newItem.setOwner(owner);
        log.info("New item {} created", newItem.getId());
        return ItemMapper.toGetItemDtoFromItem(itemRepository.save(newItem));
    }

    @Override
    public ItemForResponseDto updateItem(Long ownerId, Long itemId, CreateUpdateItemDto updateItem) {
        User user = validationService.isExistUser(ownerId);
        Item item = validationService.isExistItem(itemId);
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException(
                    String.format("User with ID = %s does not have Item with ID = %s", user.getId(), item.getId()));
        }
        if (updateItem.getName() != null && !updateItem.getName().isBlank()) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().isBlank()) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        return ItemMapper.toGetItemDtoFromItem(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemForResponseDto getItemById(Long ownerId, Long itemId) {
        validationService.isExistUser(ownerId);
        Item existItem = validationService.isExistItem(itemId);
        log.info("Item is shown");
        if (Objects.equals(existItem.getOwner().getId(), ownerId)) {
            return ItemMapper.toItemWIthBookingDto(existItem);
        } else {
            return ItemMapper.toGetItemDtoFromItem(existItem);
        }
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        validationService.isExistUser(ownerId);
        validationService.isExistItem(itemId);
        log.info("Item is deleted: {}", itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemForResponseDto> searchItem(Long ownerId, String text) {
        if (text == null || text.isBlank()) {
            log.info("Text is empty");
            return Collections.emptyList();
        }
        validationService.isExistUser(ownerId);

        List<ItemForResponseDto> list = itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::toGetItemDtoFromItem)
                .collect(Collectors.toList());
        String message = String.format("Items are found '%s' by text: %s", list, text);
        log.info(message);

        return list;
    }


    @Override
    public CommentDto createComment(Long bookerId, Long itemId, CreateCommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Text can not be blank.");
        }
        User user = validationService.isExistUser(bookerId);
        Item item = validationService.isExistItem(itemId);

        if (validationService.isBookingByUser(user, item)) {
            Comment comment = CommentDtoMapper.toCommentFromCreateCommentDto(commentDto);

            comment.setText(commentDto.getText());
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());

            return CommentDtoMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new NotAvailableException(String.format(
                    "User with ID = %s has not booked Item with ID = %s", bookerId, itemId));
        }
    }
}
