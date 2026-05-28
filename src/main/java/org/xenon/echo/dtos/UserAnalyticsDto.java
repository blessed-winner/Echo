package org.xenon.echo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
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
