package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
            User user, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            User user, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);


    @Query("SELECT b FROM Booking b WHERE b.booker = :user AND b.end < :localDateTime ORDER BY b.start DESC")
    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(
            @Param("user") User user, @Param("localDateTime") LocalDateTime localDateTime, Pageable pageable);


    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);


    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(
            User user, Status status, Pageable pageable);


    List<Booking> findAllByItem_OwnerOrderByStartDesc(User userId, Pageable pageable);


    @Query("select b from Booking b where b.item.owner = :user and b.end < :time order by b.start DESC")
    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
            @Param("user") User user,
            @Param("time") LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);


    List<Booking> findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
            User user, Status status, Pageable pageable);
}
