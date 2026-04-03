package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.*;

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
    private String id;

    private String name;

    private String email;

    private String password;

    @Column(name = "creation_date")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch =  FetchType.LAZY, cascade =   CascadeType.ALL)
    private Set<MemoryItem> memoryItems = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role;
}
