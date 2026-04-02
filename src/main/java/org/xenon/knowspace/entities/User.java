package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String email;

    private String password;

    @Column(name = "creation-date")
    private Date createdAt;

    @OneToMany(mappedBy = "user")
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<MemoryItem> memoryItems = new HashSet<>();
}
