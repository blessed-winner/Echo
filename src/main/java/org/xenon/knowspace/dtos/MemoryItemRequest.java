package org.xenon.knowspace.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MemoryItemRequest {
    @NotBlank(message = "Text is required")
    @Size(max = 1000)
    private String text;

    private String source;

    @NotBlank(message = "Each memory item must have a note")
    private Long NoteId;

    private Set<Long> tagIds;
}
