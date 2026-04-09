package org.xenon.knowspace.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.xenon.knowspace.enums.ReviewRating;

@Getter
@Setter
public class ReviewRequest {
    @NotNull
    private ReviewRating rating;
}
