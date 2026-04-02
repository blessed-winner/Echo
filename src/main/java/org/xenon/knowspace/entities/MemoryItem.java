package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "memory_items")
@Data
public class MemoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private String source;

    private Date lastReviewed;

    private String userId;
}
