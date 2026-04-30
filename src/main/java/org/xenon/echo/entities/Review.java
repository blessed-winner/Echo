package org.xenon.echo.entities;

import jakarta.persistence.*;
import org.xenon.echo.enums.ReviewRating;

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

    @Enumerated(EnumType.STRING)
    private ReviewRating rating;

    private long timeSpentSeconds;

    private int intervalBeforeReview;

    private double easeFactorBefore;
}
