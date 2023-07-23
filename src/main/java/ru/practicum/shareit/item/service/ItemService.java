package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

import java.util.List;

public interface ItemService {
    List<ItemForResponseDto> getAllItems(Long userId);

    ItemForResponseDto createItem(Long ownerId, CreateUpdateItemDto item);

    ItemForResponseDto updateItem(Long ownerId, Long itemId, CreateUpdateItemDto item);

    ItemForResponseDto getItemById(Long ownerId, Long itemId);

    void deleteItem(Long ownerId, Long itemId);

    List<ItemForResponseDto> searchItem(Long ownerId, String str);

    CommentDto createComment(Long bookerId, Long itemId, CreateCommentDto commentDto);

}
