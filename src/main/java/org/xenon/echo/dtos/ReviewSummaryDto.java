package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSummaryDto {
    private long totalReviews;
    private long totalReviewedToday;
    private long totalReviewedThisWeek;
    private double successfulReviews;
}
