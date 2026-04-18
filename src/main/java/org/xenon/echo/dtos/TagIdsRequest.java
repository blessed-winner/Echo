package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TagIdsRequest {
    Set<Long> tagIds;
}
