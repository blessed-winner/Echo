package org.xenon.echo.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.enums.ReviewRating;

@Getter
@Setter
public class ReviewRequest {
    @NotNull
    private ReviewRating rating;
}
