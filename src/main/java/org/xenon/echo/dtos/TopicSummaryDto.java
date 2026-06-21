package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicSummaryDto {
    private long totalNotes;
    private long totalMemoryItems;
    private long dueItems;
}
