package ru.practicum.gate.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gate.constants.Constants;
import ru.practicum.gate.request.dto.ItemRequestDto;
import ru.practicum.gate.validation.CreateObject;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(value = Constants.USER_ID_HEADER) Long requesterId,
                                                 @RequestBody @Valid @Validated(CreateObject.class) ItemRequestDto itemRequestDto) {
        log.info("Adding new Item request. Request = {}", itemRequestDto);
        return itemRequestClient.addItemRequest(requesterId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(
            @RequestHeader(Constants.USER_ID_HEADER) Long requesterId) {
        log.info("Getting a list of Item Requests for User ID = '{}'.", requesterId);
        return itemRequestClient.getItemRequestsByUserId(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(Constants.USER_ID_HEADER) Long requesterId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam Integer size) {
        log.info("Getting a list of Item Requests for All");
        return itemRequestClient.getAllRequests(requesterId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                     @PathVariable Long requestId) {
        log.info("Getting Item Requests by its ID: {}", requestId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
