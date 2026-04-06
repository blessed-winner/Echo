package org.xenon.knowspace.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_item_id")
    private MemoryItem memoryItem;

    private LocalDateTime reviewDate;
    private int rating; // 1-5
}
