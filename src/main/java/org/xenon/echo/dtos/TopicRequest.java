package org.xenon.echo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicRequest {
    @NotBlank(message = "Topic name is required")
    private String name;

    @Size(max = 200)
    private String description;
}
