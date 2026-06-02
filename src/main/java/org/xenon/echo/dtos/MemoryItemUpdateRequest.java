package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MemoryItemUpdateRequest {
    private String front;
    private String back;
    private String source;
    private Set<Long> tagIds;
}
