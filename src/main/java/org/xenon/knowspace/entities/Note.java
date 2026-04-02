package org.xenon.knowspace.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @Column(name = "creation_date")
    private Date createdAt;

    private Long topicId;
}
