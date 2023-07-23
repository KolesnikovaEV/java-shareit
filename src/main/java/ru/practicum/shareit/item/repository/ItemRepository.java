package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', ?1, '%')) "
            + "or lower(item.description) like lower(concat('%', ?1, '%'))" +
            " and item.available = true ")
    List<Item> searchItems(String text);

    @Query("select i " +
            "from Item i " +
            "join fetch i.owner " +
            "where i.owner.id = :id ")
    List<Item> findAllByOwnerIdWithBookings(@Param("id") Long userId);

    @Query("select i " +
            "from Item i " +
            "join fetch i.owner " +
            "where i.id = :id ")
    Optional<Item> findByIdWithOwner(@Param("id") Long itemId);
}
