package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicUpdateRequest {
    private String name;
    private String description;
}
