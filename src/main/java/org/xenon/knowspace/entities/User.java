package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;

    private String email;

    private String password;

    @Column(name = "creation-date")
    private Date createdAt;
}
