package ru.practicum.gate.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gate.constants.Constants;
import ru.practicum.gate.validation.CreateObject;
import ru.practicum.gate.validation.UpdateObject;
import ru.practicum.gate.item.dto.CreateCommentDto;
import ru.practicum.gate.item.dto.CreateUpdateItemDto;

import javax.validation.Valid;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;


    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Getting All Items by User ID: {}", userId);
        return itemClient.getAllItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(value = Constants.USER_ID_HEADER) Long ownerId,
                                         @RequestBody @Valid @Validated(CreateObject.class) CreateUpdateItemDto item) {
        log.info("Creating Item");
        return itemClient.createItem(ownerId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = Constants.USER_ID_HEADER) Long ownerId,
                                         @PathVariable Long itemId, @Validated(UpdateObject.class)
                                         @RequestBody CreateUpdateItemDto item) {
        log.info("Updating Item: {}", itemId);
        return itemClient.updateItem(ownerId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(value = Constants.USER_ID_HEADER) Long ownerId,
                                          @PathVariable Long itemId) {
        log.info("Getting Item by Item ID: {}", itemId);
        return itemClient.getItemById(ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(value = Constants.USER_ID_HEADER) Long ownerId,
                           @PathVariable Long itemId) {
        log.info("Deleting Item ID: {}", itemId);
        itemClient.deleteItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(value = Constants.USER_ID_HEADER) Long ownerId,
                                             @RequestParam(value = "text", required = false) String text) {
        log.info("Search Items by incoming Text");
        return itemClient.searchItem(ownerId, text);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid CreateCommentDto commentDto) {
        log.info("Creating Comment for Item by User ID: {}", userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
