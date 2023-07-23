package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.CreateObject;
import ru.practicum.shareit.validation.UpdateObject;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemForResponseDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItems(userId);
    }

    @PostMapping
    public ItemForResponseDto createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                         @RequestBody @Valid @Validated(CreateObject.class) CreateUpdateItemDto item) {
        return itemService.createItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemForResponseDto updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                         @PathVariable Long itemId, @Validated(UpdateObject.class)
                                         @RequestBody CreateUpdateItemDto item) {
        return itemService.updateItem(ownerId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemForResponseDto getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                          @PathVariable Long itemId) {
        return itemService.getItemById(ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemForResponseDto> searchItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long ownerId,
                                               @RequestParam(value = "text", required = false) String text) {
        return itemService.searchItem(ownerId, text);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid CreateCommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
