package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users", schema = "public")
@EqualsAndHashCode(exclude = {"name", "email"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
