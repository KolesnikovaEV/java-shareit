package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ValidationService validationService;

    @Override
    public ItemRequestForResponse addItemRequest(Long requesterId, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()
                || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Description can not be empty");
        }
        User userFromDb = validationService.isExistUser(requesterId);

        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto);

        itemRequest.setRequester(userFromDb);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestForResponse(itemRequestRepository.saveAndFlush(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestForResponse> getItemRequestsByUserId(Long requesterId) {
        validationService.isExistUser(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(requesterId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestForResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestForResponse> getAllRequests(Long userId, Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Error with Pagination from or size");
        }
        validationService.isExistUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests =
                itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(userId, pageable);
        log.info("All Requests are shown for User ID = '{}'.", userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestForResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestForResponse getItemRequestById(Long userId, Long requestId) {
        validationService.isExistUser(userId);
        ItemRequest result = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Item Request is not found"));
        return ItemRequestMapper.toItemRequestForResponse(result);
    }
}
