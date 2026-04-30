package org.xenon.echo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSystemAnalyticsDto {
    private long totalUsers;
    private long activeUsers;
    private long totalNotes;
    private long totalMemoryItems;
    private long totalReviews;
    private long totalTags;
}
