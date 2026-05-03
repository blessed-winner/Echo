package org.xenon.echo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xenon.echo.entities.Review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    Page<Review> findByMemoryItemIdAndMemoryItemUserId(Long memoryItemId, UUID userId, Pageable pageable);

    @Query("""
      SELECT COUNT(r) FROM Review r WHERE r.memoryItem.user.id = :userId
     """)
    Long countByUserId(UUID userId);

    @Query("""
      SELECT COUNT(r) FROM Review r WHERE r.memoryItem.user.id = :userId AND r.reviewDate >= :startOfDay
    """)
    Long countToday(UUID userId, LocalDateTime startOfDay);

    @Query("""
     SELECT COUNT(r) FROM Review r WHERE r.memoryItem.user.id = :userId AND r.reviewDate >= :startOfWeek
    """)
    Long countReviewsThisWeek(UUID userId, LocalDateTime startOfWeek);

    @Query("""
      SELECT COUNT(r) FROM Review  r WHERE r.memoryItem.user.id = :userId AND r.rating <> org.xenon.echo.enums.ReviewRating.AGAIN AND r.rating <> org.xenon.echo.enums.ReviewRating.HARD
    """)
    Double countSuccessfulReviews(UUID userId);

    Page<Review> findByMemoryItemUserIdOrderByReviewDateDesc(UUID userId, Pageable pageable);

    @Query("""
     SELECT DISTINCT r.reviewDate FROM Review r WHERE r.memoryItem.user.id = :userId ORDER BY r.reviewDate DESC
    """)
    List<LocalDateTime> findDistinctReviewDatesByUserId(UUID userId);
}
