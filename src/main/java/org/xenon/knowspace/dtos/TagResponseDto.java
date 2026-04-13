package org.xenon.knowspace.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagResponseDto {
    private Long id;
    private String name;
    private int count;
}
