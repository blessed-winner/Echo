package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.echo.dtos.ReviewDto;
import org.xenon.echo.dtos.ReviewSummaryDto;
import org.xenon.echo.dtos.UserAnalyticsDto;
import org.xenon.echo.entities.Review;
import org.xenon.echo.exceptions.MemoryItemNotFoundException;
import org.xenon.echo.repositories.MemoryItemRepository;
import org.xenon.echo.repositories.ReviewRepository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemoryItemRepository memoryItemRepository;

    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<ReviewDto> getReviewsPerItem(Long id,int page, int size){
        UUID userId = getCurrentUser();
       var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item not found"));
        Pageable pageable = PageRequest.of(page,size, Sort.by("reviewDate").descending());
       Page<Review> reviews = reviewRepository.findByMemoryItemIdAndMemoryItemUserId(memoryItem.getId(),userId,pageable);
       return reviews.map(review -> {
           ReviewDto dto = new ReviewDto();
           dto.setMemoryItemId(memoryItem.getId());
           dto.setReviewDate(review.getReviewDate());
           dto.setIntervalBeforeReview(review.getIntervalBeforeReview());
           dto.setRating(review.getRating());
           dto.setTimeSpentSeconds(review.getTimeSpentSeconds());
           dto.setEaseFactorBefore(review.getEaseFactorBefore());
           return dto;
       });
    }

    public ReviewSummaryDto getReviewSummary(){
        UUID userId = getCurrentUser();
        ReviewSummaryDto reviewSummary = new ReviewSummaryDto();
        reviewSummary.setTotalReviews(reviewRepository.countByUserId(userId));
        reviewSummary.setTotalReviewedToday(reviewRepository.countToday(userId, LocalDateTime.now().toLocalDate().atStartOfDay()));
        reviewSummary.setTotalReviewedThisWeek(reviewRepository.countReviewsThisWeek(userId,LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay()));
        reviewSummary.setSuccessfulReviews(calculateSuccessfulReviews(userId));

        return reviewSummary;
    }

    public Page<ReviewDto> getRecentReviews(int limit){
        UUID userId = getCurrentUser();
        Pageable pageable = PageRequest.of(0,limit, Sort.by("reviewDate").descending());
        Page<Review> reviews = reviewRepository.findByMemoryItemUserIdOrderByReviewDateDesc(userId,pageable);
        return reviews.map(review -> {
            ReviewDto dto = new ReviewDto();
            dto.setMemoryItemId(review.getMemoryItem().getId());
            dto.setReviewDate(review.getReviewDate());
            dto.setIntervalBeforeReview(review.getIntervalBeforeReview());
            dto.setRating(review.getRating());
            dto.setTimeSpentSeconds(review.getTimeSpentSeconds());
            dto.setEaseFactorBefore(review.getEaseFactorBefore());
            return dto;
        });
    }

    private double calculateSuccessfulReviews(UUID userId){
        long total = reviewRepository.countByUserId(userId);
        if(total == 0){
            return 0.0;
        }
        double successful = reviewRepository.countSuccessfulReviews(userId);

        return (successful /total) * 100;
    }
}
