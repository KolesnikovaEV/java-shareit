package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestForResponse addItemRequest(Long requesterId, ItemRequestDto itemRequestDto);

    List<ItemRequestForResponse> getItemRequestsByUserId(Long requesterId);

    List<ItemRequestForResponse> getAllRequests(Long requesterId, Integer from, Integer size);

    ItemRequestForResponse getItemRequestById(Long userId, Long requestId);
}
