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
import org.xenon.echo.entities.Review;
import org.xenon.echo.exceptions.MemoryItemNotFoundException;
import org.xenon.echo.mappers.ReviewMapper;
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
    private final ReviewMapper reviewMapper;

    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Page<ReviewDto> getReviewsPerItem(Long id,int page, int size){
        UUID userId = getCurrentUser();
       var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item not found"));
        Pageable pageable = PageRequest.of(page,size, Sort.by("reviewDate").descending());
       Page<Review> reviews = reviewRepository.findByMemoryItemIdAndMemoryItemUserId(memoryItem.getId(),userId,pageable);
       return reviews.map(reviewMapper::toDto);
    }

    public ReviewSummaryDto getReviewSummary(){
        UUID userId = getCurrentUser();
        ReviewSummaryDto reviewSummary = new ReviewSummaryDto();
        var totalReviews = reviewRepository.countByUserId(userId);
        System.out.println(totalReviews);
        reviewSummary.setTotalReviews(totalReviews);
        reviewSummary.setTotalReviewedToday(reviewRepository.countToday(userId, LocalDateTime.now().toLocalDate().atStartOfDay()));
        reviewSummary.setTotalReviewedThisWeek(reviewRepository.countReviewsThisWeek(userId,LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay()));
        reviewSummary.setSuccessfulReviews(calculateSuccessfulReviews(userId));

        return reviewSummary;
    }

    public Page<ReviewDto> getRecentReviews(int limit){
        UUID userId = getCurrentUser();
        Pageable pageable = PageRequest.of(0,limit, Sort.by("reviewDate").descending());
        Page<Review> reviews = reviewRepository.findRecentReviews(userId,pageable);
        return reviews.map(reviewMapper::toDto);
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
