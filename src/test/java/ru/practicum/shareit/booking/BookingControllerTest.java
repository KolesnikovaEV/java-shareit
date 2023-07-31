package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private CreateBookingDto createBookingDto;
    private User owner;
    private User booker;
    private Item item;
    private static LocalDateTime start;
    private static LocalDateTime end;

    @BeforeAll
    static void beforeAll() {
        start = LocalDateTime.now();
        end = LocalDateTime.now();
    }

    @BeforeEach
    void setup() {
        booker = User.builder()
                .id(101L)
                .name("Name booker")
                .email("booker@ya.ru")
                .build();

        createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        owner = User.builder()
                .id(1L)
                .name("Name User owner")
                .email("owner@m.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Pen")
                .description("Can write well")
                .owner(owner)
                .available(true)
                .build();

    }

    @SneakyThrows
    @Test
    public void createBooking_ShouldReturnBookingForResponse() {
        Long bookerId = 1L;

        BookingForResponse bookingForResponse = BookingForResponse.builder()
                .id(1L)
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .status(Status.WAITING)
                .booker(UserMapper.userOnlyWithIdDto(booker))
                .item(ItemMapper.toGetBookingDtoFromItem(item)).build();
        when(bookingService.createBooking(bookerId, createBookingDto)).thenReturn(bookingForResponse);

        BookingForResponse result = bookingService.createBooking(bookerId, createBookingDto);

        assertEquals(bookingForResponse, result);
    }

    @SneakyThrows
    @Test
    void updateByOwner() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .status(Status.WAITING)
                .booker(UserMapper.userOnlyWithIdDto(booker))
                .item(ItemMapper.toGetBookingDtoFromItem(item)).build();

        when(bookingService.updateBooking(any(), any(), any()))
                .thenReturn(bookingDto1ForResponse);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void getWithStatusById() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .status(Status.WAITING)
                .booker(UserMapper.userOnlyWithIdDto(booker))
                .item(ItemMapper.toGetBookingDtoFromItem(item)).build();

        when(bookingService.getBookingByOwner(any(), any()))
                .thenReturn(bookingDto1ForResponse);
        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingDto1ForResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId()))

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDto1ForResponse), result);
    }

    @SneakyThrows
    @Test
    void getAllUserBookings() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .status(Status.WAITING)
                .booker(UserMapper.userOnlyWithIdDto(booker))
                .item(ItemMapper.toGetBookingDtoFromItem(item)).build();
        when(bookingService.getAllUserBookings(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookings() {
        BookingForResponse bookingDto1ForResponse = BookingForResponse.builder()
                .id(1L)
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .status(Status.WAITING)
                .booker(UserMapper.userOnlyWithIdDto(booker))
                .item(ItemMapper.toGetBookingDtoFromItem(item)).build();

        when(bookingService.getAllOwnerBookings(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto1ForResponse));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto1ForResponse)), result);
    }

    @SneakyThrows
    @Test
    public void createBooking_WithInvalidData_ShouldReturn400Error() {
        createBookingDto.setStart(start.plusDays(5));
        createBookingDto.setEnd(end.plusDays(2));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(objectMapper.writeValueAsString(createBookingDto))
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "12345"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getAllUserBookings_WithInvalidData_ShouldReturn400Error() {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getAllOwnerBookings_WithInvalidData_ShouldReturn400Error() {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }
}
