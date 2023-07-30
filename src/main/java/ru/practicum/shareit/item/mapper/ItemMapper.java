package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoMapper;
import ru.practicum.shareit.item.dto.CreateUpdateItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constant.Constants.orderByStartDateAsc;
import static ru.practicum.shareit.constant.Constants.orderByStartDateDesc;

@UtilityClass
public class ItemMapper {

    public ItemForResponseDto toGetItemDtoFromItem(Item item) {
        List<CommentDto> comments = new ArrayList<>();

        if (item.getComments() != null) {
            comments.addAll(item.getComments()
                    .stream()
                    .map(CommentDtoMapper::toCommentDto)
                    .collect(Collectors.toSet()));
        }

        return ItemForResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .requestId(item.getRequestId())
                .build();
    }

    public ItemForResponseDto toItemWIthBookingDto(Item item) {
        LocalDateTime currentTime = LocalDateTime.now();

        ItemForResponseDto getItemDto = toGetItemDtoFromItem(item);

        Set<Booking> bookings = item.getBookings();

        Booking lastBooking = bookings
                .stream()
                .sorted(orderByStartDateDesc)
                .filter(t -> t.getStart().isBefore(currentTime) &&
                        t.getStatus().equals(Status.APPROVED))
                .findFirst()
                .orElse(null);

        Booking nextBooking = bookings
                .stream()
                .sorted(orderByStartDateAsc)
                .filter(t -> t.getStart().isAfter(currentTime) &&
                        t.getStatus().equals(Status.APPROVED))
                .findFirst()
                .orElse(null);

        getItemDto.setLastBooking(BookingMapper.toBookingForItemDto(lastBooking));
        getItemDto.setNextBooking(BookingMapper.toBookingForItemDto(nextBooking));
        getItemDto.setRequestId(item.getRequestId());

        return getItemDto;
    }

    public Item toGetItemFromCreateUpdateItemDto(CreateUpdateItemDto createUpdateItemDto) {
        return Item.builder()
                .name(createUpdateItemDto.getName())
                .description(createUpdateItemDto.getDescription())
                .available(createUpdateItemDto.getAvailable())
                .requestId(createUpdateItemDto.getRequestId())
                .build();
    }

    public ItemWithBookingDto toGetBookingDtoFromItem(Item item) {
        return ItemWithBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public Item toItemFromItemRequest(ItemForResponseDto item) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .requestId(item.getRequestId())
                .build();
    }
}