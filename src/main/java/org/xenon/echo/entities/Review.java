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

    @Column(name = "reviewed_at")
    private LocalDateTime reviewDate;


    @Enumerated(EnumType.STRING)
    private ReviewRating rating;

    @Column(name = "time_spent_seconds")
    private long timeSpentSeconds;

    @Column(name = "interval_before_review")
    private int intervalBeforeReview;

    @Column(name = "ease_factor_before")
    private double easeFactorBefore;
}
