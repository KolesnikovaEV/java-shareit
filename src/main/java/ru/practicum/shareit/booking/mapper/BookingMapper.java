package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {

    public Booking toBookingFromCreateBookingDto(CreateBookingDto createBookingDto) {
        return Booking.builder()
                .start(createBookingDto.getStart())
                .end(createBookingDto.getEnd())
                .build();
    }

    public BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingForItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .build();
    }

    public BookingForResponse toGetBookingForResponse(Booking booking) {
        return BookingForResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(UserMapper.userOnlyWithIdDto(booking.getBooker()))
                .item(ItemMapper.toGetBookingDtoFromItem(booking.getItem()))
                .build();
    }
}
