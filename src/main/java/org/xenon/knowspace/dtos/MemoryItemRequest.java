package org.xenon.knowspace.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MemoryItemRequest {
    @NotBlank(message = "Text is required")
    private String text;

    private String source;

    @NotBlank(message = "Each memory item must have a note")
    private Long NoteId;

    private Set<Long> tagIds;
}
