package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponse;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;

    @MockBean
    ItemRepository itemRepository;
    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testAddItemRequest() throws Exception {
        Long requesterId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test Request");

        ItemRequestForResponse expectedResponse = new ItemRequestForResponse();
        expectedResponse.setId(1L);
        expectedResponse.setDescription("Test Request");

        when(itemRequestService.addItemRequest(eq(requesterId), any(ItemRequestDto.class)))
                .thenReturn(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/requests")
                .header("X-Sharer-User-Id", requesterId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Test Request\"}");

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Request"));

        verify(itemRequestService, times(1)).addItemRequest(eq(requesterId), any(ItemRequestDto.class));
    }

    @Test
    public void testGetItemRequestsByUserId() throws Exception {
        Long requesterId = 1L;

        ItemRequestForResponse itemRequest1 = new ItemRequestForResponse();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Request 1");

        ItemRequestForResponse itemRequest2 = new ItemRequestForResponse();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Request 2");

        List<ItemRequestForResponse> expectedResponse = Arrays.asList(itemRequest1, itemRequest2);

        when(itemRequestService.getItemRequestsByUserId(requesterId)).thenReturn(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/requests")
                .header("X-Sharer-User-Id", requesterId);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Request 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Request 2"));

        verify(itemRequestService, times(1)).getItemRequestsByUserId(requesterId);
    }

    @Test
    public void testGetAllRequests() throws Exception {
        Long requesterId = 1L;

        ItemRequestForResponse itemRequest1 = new ItemRequestForResponse();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Request 1");

        ItemRequestForResponse itemRequest2 = new ItemRequestForResponse();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Request 2");

        List<ItemRequestForResponse> expectedResponse = Arrays.asList(itemRequest1, itemRequest2);

        when(itemRequestService.getAllRequests(requesterId, 0, 20)).thenReturn(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/requests/all")
                .header("X-Sharer-User-Id", requesterId)
                .param("from", "0")
                .param("size", "20");

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Request 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Request 2"));

        verify(itemRequestService, times(1)).getAllRequests(requesterId, 0, 20);
    }

    @Test
    public void testGetItemRequestById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestForResponse expectedResponse = new ItemRequestForResponse();
        expectedResponse.setId(1L);
        expectedResponse.setDescription("Test Request");

        when(itemRequestService.getItemRequestById(userId, requestId)).thenReturn(expectedResponse);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                .header("X-Sharer-User-Id", userId);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Test Request"));

        verify(itemRequestService, times(1)).getItemRequestById(userId, requestId);
    }
}

