package org.xenon.knowspace.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteUpdateRequest {
    private String title;

    private String content;

     private Long[] tagIds;
}
