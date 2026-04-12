package org.xenon.knowspace.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xenon.knowspace.entities.Note;

import java.time.LocalDateTime;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note,Long> {
    Page<Note> findAllByUserId(UUID userId, Pageable pageable);

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
}