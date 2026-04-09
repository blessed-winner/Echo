package org.xenon.knowspace.dtos;

import lombok.Getter;
import lombok.Setter;
import org.xenon.knowspace.enums.ReviewRating;

@Getter
@Setter
public class ReviewRequest {
    private ReviewRating rating;
}
