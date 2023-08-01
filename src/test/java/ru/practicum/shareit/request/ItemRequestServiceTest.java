package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ValidationService validationService;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void testAddItemRequest_ValidRequest_ReturnsItemRequestForResponse() {
        Long requesterId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Request 1");

        User userFromDb = new User();
        userFromDb.setId(requesterId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request 1");
        itemRequest.setRequester(userFromDb);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestForResponse expectedResponse = new ItemRequestForResponse();
        expectedResponse.setId(1L);
        expectedResponse.setDescription("Request 1");

        when(validationService.isExistUser(requesterId)).thenReturn(userFromDb);
        when(itemRequestRepository.saveAndFlush(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestForResponse actualResponse = itemRequestService.addItemRequest(requesterId, itemRequestDto);

        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());

        verify(validationService, times(1)).isExistUser(requesterId);
        verify(itemRequestRepository, times(1)).saveAndFlush(any(ItemRequest.class));
    }

    @Test
    public void testGetItemRequestsByUserId_ValidUserId_ReturnsListOfItemRequestForResponse() {
        Long requesterId = 1L;

        User userFromDb = new User();
        userFromDb.setId(requesterId);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Request 1");
        itemRequest1.setRequester(userFromDb);
        itemRequest1.setCreated(LocalDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Request 2");
        itemRequest2.setRequester(userFromDb);
        itemRequest2.setCreated(LocalDateTime.now());

        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        List<ItemRequestForResponse> expectedResponse = Arrays.asList(
                ItemRequestForResponse.builder().id(1L).description("Request 1").build(),
                ItemRequestForResponse.builder().id(2L).description("Request 2").build()
        );

        when(validationService.isExistUser(requesterId)).thenReturn(userFromDb);
        when(itemRequestRepository.getAllByRequester_IdOrderByCreatedDesc(requesterId)).thenReturn(itemRequests);

        List<ItemRequestForResponse> actualResponse = itemRequestService.getItemRequestsByUserId(requesterId);

        assertEquals(expectedResponse.size(), actualResponse.size());
        for (int i = 0; i < expectedResponse.size(); i++) {
            assertEquals(expectedResponse.get(i).getId(), actualResponse.get(i).getId());
            assertEquals(expectedResponse.get(i).getDescription(), actualResponse.get(i).getDescription());
        }

        verify(validationService, times(1)).isExistUser(requesterId);
        verify(itemRequestRepository, times(1)).getAllByRequester_IdOrderByCreatedDesc(requesterId);
    }

    @Test
    public void testGetAllRequests_ValidUserId_ReturnsListOfItemRequestForResponse() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 20;

        User userFromDb = new User();
        userFromDb.setId(userId);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Request 1");
        itemRequest1.setRequester(userFromDb);
        itemRequest1.setCreated(LocalDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Request 2");
        itemRequest2.setRequester(userFromDb);
        itemRequest2.setCreated(LocalDateTime.now());

        List<ItemRequest> itemRequests = Arrays.asList(itemRequest1, itemRequest2);

        List<ItemRequestForResponse> expectedResponse = Arrays.asList(
                ItemRequestForResponse.builder().id(1L).description("Request 1").build(),
                ItemRequestForResponse.builder().id(2L).description("Request 2").build()
        );

        Pageable pageable = PageRequest.of(from / size, size);

        when(validationService.isExistUser(userId)).thenReturn(userFromDb);
        when(itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(userId, pageable)).thenReturn(itemRequests);

        List<ItemRequestForResponse> actualResponse = itemRequestService.getAllRequests(userId, from, size);

        assertEquals(expectedResponse.size(), actualResponse.size());
        for (int i = 0; i < expectedResponse.size(); i++) {
            assertEquals(expectedResponse.get(i).getId(), actualResponse.get(i).getId());
            assertEquals(expectedResponse.get(i).getDescription(), actualResponse.get(i).getDescription());
        }

        verify(validationService, times(1)).isExistUser(userId);
        verify(itemRequestRepository, times(1)).getItemRequestByRequesterIdIsNotOrderByCreated(userId, pageable);
    }

    @Test
    public void testGetItemRequestById_ValidUserIdAndRequestId_ReturnsItemRequestForResponse() {
        Long userId = 1L;
        Long requestId = 1L;

        User userFromDb = new User();
        userFromDb.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Request 1");
        itemRequest.setRequester(userFromDb);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestForResponse expectedResponse = ItemRequestForResponse.builder()
                .id(requestId)
                .description("Request 1")
                .build();

        when(validationService.isExistUser(userId)).thenReturn(userFromDb);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestForResponse actualResponse = itemRequestService.getItemRequestById(userId, requestId);

        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());

        verify(validationService, times(1)).isExistUser(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
    }

    @Test
    public void testGetItemRequestById_InvalidRequestId_ThrowsNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        User userFromDb = new User();
        userFromDb.setId(userId);

        when(validationService.isExistUser(userId)).thenReturn(userFromDb);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getItemRequestById(userId, requestId);
        });

        verify(validationService, times(1)).isExistUser(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
    }

    @Test
    public void testAddItemRequest_EmptyDescription_ThrowsValidationException() {
        Long requesterId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("");

        assertThrows(ValidationException.class, () -> itemRequestService.addItemRequest(requesterId, itemRequestDto));
        verify(validationService, never()).isExistUser(requesterId);
    }

    @Test
    public void testAddItemRequest_NullDescription_ThrowsValidationException() {
        Long requesterId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(null);

        assertThrows(ValidationException.class, () -> itemRequestService.addItemRequest(requesterId, itemRequestDto));
        verify(validationService, never()).isExistUser(requesterId);
    }

    @Test
    public void testGetItemRequestsByUserId() {
        Long requesterId = 1L;

        itemRequestService.getItemRequestsByUserId(requesterId);

        verify(validationService).isExistUser(requesterId);
        verify(itemRequestRepository).getAllByRequester_IdOrderByCreatedDesc(requesterId);
    }

    @Test
    public void testGetAllRequests_ValidPaginationParams_ReturnsItemRequestForResponseList() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> itemRequests = new ArrayList<>();
        when(itemRequestRepository.getItemRequestByRequesterIdIsNotOrderByCreated(userId, pageable)).thenReturn(itemRequests);

        List<ItemRequestForResponse> result = itemRequestService.getAllRequests(userId, from, size);

        verify(validationService).isExistUser(userId);
        verify(itemRequestRepository).getItemRequestByRequesterIdIsNotOrderByCreated(userId, pageable);
        assertEquals(itemRequests.size(), result.size());
    }
}
