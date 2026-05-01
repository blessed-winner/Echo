package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.echo.dtos.ReviewSummaryDto;
import org.xenon.echo.services.ReviewService;

@Tag(name = "Reviews")
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    public ResponseEntity<ReviewSummaryDto> getReviewSummary(){
        return ResponseEntity.ok(reviewService.getReviewSummary());
    }
}
