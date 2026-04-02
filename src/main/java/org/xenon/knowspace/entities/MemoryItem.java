package org.xenon.knowspace.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "memory_items")
public class MemoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private String source;

    private Date lastReviewed;

    private String userId;
}
