package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.enums.ReviewRating;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDto {
    private long memoryItemId;
    private LocalDateTime reviewDate;
    private ReviewRating rating;
    private long timeSpentSeconds;
    private int intervalBeforeReview;
    private double easeFactorBefore;
}
