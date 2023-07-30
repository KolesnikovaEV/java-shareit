package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForResponse;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ValidationService validationService;

    @Override
    public BookingForResponse createBooking(Long bookerId, CreateBookingDto createBookingDto) {

        User bookerFromDb = validationService.isExistUser(bookerId);

        Item itemFromDB = validationService.isExistItem(createBookingDto.getItemId());
        if (!itemFromDB.getAvailable()) {
            throw new NotAvailableException("Item can not be booked, because available = false.");
        }

        validationService.validateBooking(createBookingDto, itemFromDB, bookerFromDb);

        Booking booking = BookingMapper.toBookingFromCreateBookingDto(createBookingDto);
        booking.setStatus(Status.WAITING);
        booking.setItem(itemFromDB);
        booking.setBooker(bookerFromDb);
        return BookingMapper.toGetBookingForResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingForResponse updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        validationService.isExistUser(ownerId);
        Booking booking = validationService.isExistBooking(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new NotFoundException("Booking is not found");
        }

        Status status;

        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new NotAvailableException("Booking is already approved.");
            }
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }

        booking.setStatus(status);

        return BookingMapper.toGetBookingForResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingForResponse getBookingByOwner(Long userId, Long bookingId) {
        validationService.isExistUser(userId);
        Booking booking = validationService.isExistBooking(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (userId.equals(bookerId) || userId.equals(ownerId)) {
            return BookingMapper.toGetBookingForResponse(booking);
        }
        throw new NotFoundException("Error with booking ID = '" + bookingId
                + "'. User with ID = '" + userId
                + "' is not owner or user, booked Item.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingForResponse> getAllUserBookings(Long userId, String state, Integer from, Integer size) {
        User user = validationService.isExistUser(userId);

        final LocalDateTime nowDateTime = LocalDateTime.now();

        if (from < 0 || size < 1) {
            throw new ValidationException("Error with Pagination from or size");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));

        BookingState bookingState;
        bookingState = BookingState.valueOf(state.toUpperCase());
        List<Booking> result = Collections.emptyList();

        switch (bookingState) {
            case ALL: {
                result = bookingRepository.findAllByBookerOrderByStartDesc(user, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                        user, nowDateTime, nowDateTime, pageable);
                break;
            }
            case PAST: {
                result = bookingRepository.findAllByBookerAndEndIsBeforeOrderByStartDesc(
                        user, nowDateTime, pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findAllByBookerAndStartIsAfterOrderByStartDesc(
                        user, nowDateTime, pageable);
                break;
            }
            case WAITING: {
                result = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(
                        user, Status.WAITING, pageable);
                break;
            }
            case REJECTED: {
                result = bookingRepository.findAllByBookerAndStatusEqualsOrderByStartDesc(
                        user, Status.REJECTED, pageable);
                break;
            }
            case UNSUPPORTED_STATUS: {
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        return result
                .stream()
                .map(BookingMapper::toGetBookingForResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<BookingForResponse> getAllOwnerBookings(Long userId, String state, Integer from, Integer size) {
        final LocalDateTime nowDateTime = LocalDateTime.now();

        User user = validationService.isExistUser(userId);
        if (from < 0 || size < 1) {
            throw new ValidationException("Error with Pagination from or size");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start"));

        BookingState bookingState;
        bookingState = BookingState.valueOf(state.toUpperCase());
        List<Booking> result = Collections.emptyList();

        switch (bookingState) {
            case ALL: {
                result = bookingRepository.findAllByItem_OwnerOrderByStartDesc(user, pageable);
                break;
            }
            case CURRENT: {
                result = bookingRepository.findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        user, nowDateTime, nowDateTime, pageable);
                break;
            }
            case PAST: {
                result = bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
                        user, nowDateTime, pageable);
                break;
            }
            case FUTURE: {
                result = bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(
                        user, nowDateTime, pageable);
                break;
            }
            case WAITING: {
                result = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
                        user, Status.WAITING, pageable);
                break;
            }
            case REJECTED: {
                result = bookingRepository.findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
                        user, Status.REJECTED, pageable);
                break;
            }
            case UNSUPPORTED_STATUS: {
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
        }
        return result.stream()
                .map(BookingMapper::toGetBookingForResponse)
                .collect(Collectors.toList());
    }

}
