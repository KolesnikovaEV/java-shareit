package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponse;
import ru.practicum.shareit.validation.CreateObject;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constant.Constants.USER_ID_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestForResponse addItemRequest(@RequestHeader(value = USER_ID_HEADER) Long requesterId,
                                                 @RequestBody @Valid @Validated(CreateObject.class) ItemRequestDto itemRequestDto) {
        log.info("Adding new Item request. Request = {}", itemRequestDto);
        return itemRequestService.addItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestForResponse> getItemRequestsByUserId(
            @RequestHeader(USER_ID_HEADER) Long requesterId) {
        log.info("Getting a list of Item Requests for User ID = '{}'.", requesterId);
        return itemRequestService.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestForResponse> getAllRequests(
            @RequestHeader(USER_ID_HEADER) Long requesterId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Getting a list of Item Requests for All");
        return itemRequestService.getAllRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestForResponse getItemRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @PathVariable Long requestId) {
        log.info("Getting Item Requests by its ID: {}", requestId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
