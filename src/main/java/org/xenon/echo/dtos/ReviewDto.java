package org.xenon.echo.dtos;

import org.xenon.echo.enums.ReviewRating;

import java.time.LocalDateTime;

public class ReviewDto {
    private Long memoryItemId;
    private LocalDateTime reviewDate;
    private ReviewRating rating;
    private long timeSpentSeconds;
    private int intervalBeforeReview;
    private double easeFactorBefore;
}
