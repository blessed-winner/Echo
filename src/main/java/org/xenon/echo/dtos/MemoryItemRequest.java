package org.xenon.echo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MemoryItemRequest {
    @NotBlank(message = "Front content is required")
    @Size(max = 200)
    private String front;

    @NotBlank(message = "Back content is required")
    @Size(max = 1000)
    private String back;

    private String source;

    @NotNull(message = "Each memory item must have a note")
    private Long NoteId;

    private Set<Long> tagIds;
}
