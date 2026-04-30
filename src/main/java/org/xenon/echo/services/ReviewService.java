package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.xenon.echo.dtos.ReviewDto;
import org.xenon.echo.exceptions.MemoryItemNotFoundException;
import org.xenon.echo.repositories.MemoryItemRepository;
import org.xenon.echo.repositories.ReviewRepository;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemoryItemRepository memoryItemRepository;

    public Page<ReviewDto> getReviewsPerItem(Long id){
       var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item not found"));

    }
}
