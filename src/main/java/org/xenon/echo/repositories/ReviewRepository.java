package org.xenon.echo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xenon.echo.entities.Review;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    Page<Review> findByMemoryItemIdAndMemoryItemUserId(Long memoryItemId, UUID userId, Pageable pageable);

    @Query("""
      SELECT COUNT(r) FROM Review r WHERE r.memoryItem.user.id = :userId
     """)
    long countByUserId(UUID userId);

    @Query("""
      SELECT COUNT(r) FROM Review r WHERE r.memoryItem.user.id = :userId AND r.reviewDate >= :startOfDay
    """)
    long countToday(UUID userId, LocalDateTime startOfDay);

    @Query("""
     SELECT COUNT(r) FROM Review r WHERE r.memoryItem.user.id = :userId AND r.reviewDate >= :startOfWeek
    """)
    long countReviewsThisWeek(UUID userId, LocalDateTime startOfWeek);

    @Query("""
      SELECT COUNT(r) FROM Review  r WHERE r.memoryItem.user.id = :userId AND r.rating <> org.xenon.echo.enums.ReviewRating.AGAIN AND r.rating <> org.xenon.echo.enums.ReviewRating.HARD
    """)
    double countSuccessfulReviews(UUID userId);

    Page<Review> findByMemoryItemUserIdOrderByReviewDateDesc(UUID userId, Pageable pageable);
}
