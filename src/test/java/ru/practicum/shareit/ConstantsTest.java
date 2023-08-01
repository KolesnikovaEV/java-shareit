package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Constants;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantsTest {
    @Test
    public void testOrderByStartDateDesc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();
        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 2, 12, 0))
                .build();
        int expected = 1;
        int actual = Constants.orderByStartDateDesc.compare(booking1, booking2);
        assertEquals(expected, actual);
    }

    @Test
    public void testOrderByStartDateAsc() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();
        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 2, 12, 0))
                .build();
        int expected = -1;
        int actual = Constants.orderByStartDateAsc.compare(booking1, booking2);
        assertEquals(expected, actual);
    }

    @Test
    public void testOrderByStartDateDesc_ReturnsZero() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();
        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();
        int expected = 0;
        int actual = Constants.orderByStartDateDesc.compare(booking1, booking2);
        assertEquals(expected, actual);
    }

    @Test
    public void testOrderByStartDateAsc_ReturnsZero() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();
        Booking booking2 = Booking.builder()
                .start(LocalDateTime.of(2022, 1, 1, 12, 0))
                .build();
        int expected = 0;
        int actual = Constants.orderByStartDateAsc.compare(booking1, booking2);
        assertEquals(expected, actual);
    }
}
