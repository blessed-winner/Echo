package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteSummaryDto {
    private long totalItems;
    private long dueItems;
    private long reviewedToday;
}
