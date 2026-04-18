package org.xenon.echo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class NoteRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200)
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 5000)
    private String content;

    @NotBlank(message = "Each note must belong to a topic")
    private Long topicId;

    private Set<Long> tagIds;
}
