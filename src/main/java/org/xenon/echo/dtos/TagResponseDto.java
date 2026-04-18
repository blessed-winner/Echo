package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponseDto {
    private Long id;
    private String name;
    private int count;
}
