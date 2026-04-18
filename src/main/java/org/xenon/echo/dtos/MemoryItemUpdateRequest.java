package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MemoryItemUpdateRequest {
    private String text;
    private String source;
    private Set<Long> tagIds;
}
