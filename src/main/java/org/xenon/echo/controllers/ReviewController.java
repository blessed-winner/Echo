package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.echo.dtos.ReviewDto;
import org.xenon.echo.dtos.ReviewSummaryDto;
import org.xenon.echo.services.ReviewService;

@Tag(name = "Reviews")
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/summary")
    public ResponseEntity<ReviewSummaryDto> getReviewSummary(){
        return ResponseEntity.ok(reviewService.getReviewSummary());
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<ReviewDto>> getRecentReviews(
            @RequestParam(defaultValue = "10") int limit
    ){
        return ResponseEntity.ok(reviewService.getRecentReviews(limit));
    }
}
