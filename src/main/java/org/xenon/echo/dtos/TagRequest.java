package org.xenon.echo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {
    @NotBlank(message = "Tag name is required")
    private String name;
}
