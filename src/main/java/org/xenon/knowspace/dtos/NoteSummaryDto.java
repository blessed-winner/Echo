package org.xenon.knowspace.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteSummaryDto {
    private long totalItems;
    private long dueItems;
    private long reviewedToday;
}
