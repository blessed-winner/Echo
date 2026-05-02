package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAnalyticsDto {
    private long totalNotes;
    private long totalMemoryItems;
    private long dueItems;

    private long totalReviews;
    private long reviewsToday;
    private long reviewsThisWeek;

    private double retentionRate;
    private int currentStreak;

}
