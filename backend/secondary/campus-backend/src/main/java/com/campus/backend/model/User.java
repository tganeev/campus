package com.campus.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String telegramNick;

    @Column(unique = true, nullable = true)  // Делаем email необязательным
    private String email;

    @Column(nullable = true)  // Разрешаем NULL для пароля
    private String password;

    @Column(unique = true, nullable = false)  // Новое поле для School21 логина
    private String school21Login;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String avatarUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Presence> presenceHistory = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_clubs",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "club_id")
    )
    @JsonIgnoreProperties("members")
    private Set<Club> clubs = new HashSet<>();
}