package org.xenon.knowspace.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class MemoryItemRequest {
    @NotBlank(message = "Text is required")
    private String text;

    private String source;

    @NotBlank(message = "Each memory item must have a note")
    private Long NoteId;

    private Set<Long> tagIds;
}
