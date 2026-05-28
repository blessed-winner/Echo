package org.xenon.echo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.xenon.echo.enums.Role;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String password;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    private boolean enabled = true;

    @Column(name = "creation_date")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch =  FetchType.LAZY, cascade =   CascadeType.ALL)
    private Set<MemoryItem> memoryItems = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Tag> tags = new HashSet<>();
}
