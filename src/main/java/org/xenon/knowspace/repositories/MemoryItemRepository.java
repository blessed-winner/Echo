package org.xenon.knowspace.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xenon.knowspace.entities.MemoryItem;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MemoryItemRepository extends JpaRepository<MemoryItem,Long> {
    Page<MemoryItem> findAllByUserId(UUID userId, Pageable pageable);

    Page<MemoryItem> findByUserIdAndNextReviewDateLessThanOrEqual(UUID userId, LocalDateTime referenceTime, Pageable pageable);

    @Query("""
             SELECT COUNT(m) FROM MemoryItem m WHERE m.user.id = :userId AND m.lastReviewed BETWEEN :todayStart AND :todayEnd
           """)
    long countReviewedToday(UUID userId, LocalDateTime todayStart, LocalDateTime todayEnd);

    @Query("""
            SELECT COUNT(m) FROM MemoryItem m WHERE m.user.id = :userId AND m.nextReviewDate > :now
          """)
    long countUpcoming(UUID userId, LocalDateTime now);
}