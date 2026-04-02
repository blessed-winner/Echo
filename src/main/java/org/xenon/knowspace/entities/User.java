package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<Topic> topics = new ArrayList<>();
}
