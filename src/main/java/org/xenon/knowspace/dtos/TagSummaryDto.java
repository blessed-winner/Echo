package org.xenon.knowspace.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagSummaryDto {
    private Long id;
    private String name;
    private Long totalNotes;
    private Long totalItems;
}
