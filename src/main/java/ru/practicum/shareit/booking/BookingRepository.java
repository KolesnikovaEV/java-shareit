package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(User user);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2);
//    @Query("select b from Booking b " +
//            "where b.item.owner = ?1 and b.start < ?2 and b.end > ?3 " +
//            "order by b.start DESC")

    List<Booking> findAllByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2);


    List<Booking> findAllByBookerAndEndIsBeforeOrderByStartDesc(
            User user, LocalDateTime localDateTime);


    List<Booking> findAllByBookerAndStartIsAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime);


    List<Booking> findAllByBookerAndStatusEqualsOrderByStartDesc(
            User user, Status status);


    List<Booking> findAllByItem_OwnerOrderByStartDesc(User userId);


    @Query("select b from Booking b where b.item.owner = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
            User user, LocalDateTime localDateTime);

    List<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(
            User user, LocalDateTime localDateTime);


    List<Booking> findAllByItem_OwnerAndStatusEqualsOrderByStartDesc(
            User user, Status status);
}
