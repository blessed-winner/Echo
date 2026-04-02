package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "topics")
@Data
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "creation_date")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private String userId;
}
