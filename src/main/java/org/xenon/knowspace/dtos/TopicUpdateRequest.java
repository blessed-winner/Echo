package org.xenon.knowspace.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicUpdateRequest {
    private String name;
    private String description;
}
