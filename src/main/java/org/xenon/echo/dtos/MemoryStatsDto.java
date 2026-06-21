package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoryStatsDto {
    private long todayReviewed;
    private int streak;
    private long upcoming;
    private long overdue;
}
