package org.xenon.knowspace.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xenon.knowspace.entities.MemoryItem;
import org.xenon.knowspace.entities.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MemoryItemRepository extends JpaRepository<MemoryItem,Long> {
    Page<MemoryItem> findAllByUserId(UUID userId, Pageable pageable);

    Page<MemoryItem> findByUserIdAndNextReviewDateLessThanEqual(UUID userId, LocalDateTime referenceTime, Pageable pageable);

    Page<MemoryItem> findByUserIdAndTagsIdAndNextReviewDateLessThanEqual(UUID userId,Long tagId, LocalDateTime referenceTime, Pageable pageable);

    @Query("""
             SELECT COUNT(m) FROM MemoryItem m WHERE m.user.id = :userId AND m.lastReviewed BETWEEN :todayStart AND :todayEnd
           """)
    long countReviewedToday(UUID userId, LocalDateTime todayStart, LocalDateTime todayEnd);

    @Query("""
            SELECT COUNT(m) FROM MemoryItem m WHERE m.user.id = :userId AND m.nextReviewDate > :now
          """)
    long countUpcoming(UUID userId, LocalDateTime now);

    @Query("""
            SELECT COUNT(m) FROM MemoryItem m WHERE m.user.id = :userId AND m.nextReviewDate <= :now
          """)
    long countOverdue(UUID userId,  LocalDateTime now);

    @Query("""
           SELECT m.lastReviewed FROM MemoryItem m WHERE m.user.id = :userId AND m.lastReviewed IS NOT NULL ORDER BY m.lastReviewed DESC
          """)
    List<LocalDate> findLastReviewedDates(UUID userId);

    @Query("""
    SELECT COUNT(m) FROM MemoryItem m WHERE m.note.id = :noteId AND m.note.topic.user.id = :userId
    """)
    Long countAllItemsByNoteIdAndUserId(Long noteId, UUID userId);

    @Query("""
    SELECT COUNT(m) FROM MemoryItem m WHERE m.note.id = :noteId AND m.note.topic.user.id = :userId AND m.nextReviewDate <= :now
    """)
    Long countDueItemsByNoteIdAndUserId(Long noteId, UUID userId, LocalDateTime now);

    @Query("""
     SELECT COUNT(m) FROM MemoryItem m WHERE m.note.id = :noteId AND m.note.topic.user.id = :userId AND m.lastReviewed = :now
    """)
    Long countReviewedTodayByNoteIdAndUserId(Long noteId, UUID userId, LocalDateTime now);

    Page<MemoryItem> findByNoteIdAndNoteTopicUserIdAndNextReviewDateLessThanEqual(Long noteId, UUID noteTopicUserId,LocalDateTime now, Pageable pageable);

}