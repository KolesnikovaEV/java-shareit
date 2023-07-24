package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;


@Data
@Entity
@Table(name = "items", schema = "public")
@EqualsAndHashCode(exclude = {"name", "description", "available", "owner"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User owner;

    @OneToMany
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Set<Booking> bookings;

    @OneToMany
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Set<Comment> comments;

    public User getOwner() {
        return owner.toBuilder().build();
    }
}
