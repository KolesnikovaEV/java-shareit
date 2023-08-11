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

import static ru.practicum.shareit.constant.Constants.USER_ID_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemForResponseDto> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Getting All Items by User ID: {}", userId);
        return itemService.getAllItems(userId);
    }

    @PostMapping
    public ItemForResponseDto createItem(@RequestHeader(value = USER_ID_HEADER) Long ownerId,
                                         @RequestBody @Valid @Validated(CreateObject.class) CreateUpdateItemDto item) {
        log.info("Creating Item");
        return itemService.createItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemForResponseDto updateItem(@RequestHeader(value = USER_ID_HEADER) Long ownerId,
                                         @PathVariable Long itemId, @Validated(UpdateObject.class)
                                         @RequestBody CreateUpdateItemDto item) {
        log.info("Updating Item: {}", itemId);
        return itemService.updateItem(ownerId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemForResponseDto getItemById(@RequestHeader(value = USER_ID_HEADER) Long ownerId,
                                          @PathVariable Long itemId) {
        log.info("Getting Item by Item ID: {}", itemId);
        return itemService.getItemById(ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(value = USER_ID_HEADER) Long ownerId,
                           @PathVariable Long itemId) {
        log.info("Deleting Item ID: {}", itemId);
        itemService.deleteItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemForResponseDto> searchItem(@RequestHeader(value = USER_ID_HEADER) Long ownerId,
                                               @RequestParam(value = "text", required = false) String text) {
        log.info("Search Items by incoming Text");
        return itemService.searchItem(ownerId, text);
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid CreateCommentDto commentDto) {
        log.info("Creating Comment for Item by User ID: {}", userId);
        return itemService.createComment(userId, itemId, commentDto);
    }
}
