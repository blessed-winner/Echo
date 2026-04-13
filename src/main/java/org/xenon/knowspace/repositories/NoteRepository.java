package org.xenon.knowspace.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xenon.knowspace.entities.Note;

import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note,Long> {
    Page<Note> findAllByTopicUserId(UUID userId, Pageable pageable);
    Page<Note> findByTitleContainingIgnoreCaseAndTopicUserId(String title, UUID userId, Pageable pageable);
    Page<Note> findAllByTopicId(Long id, Pageable pageable);

    @Query("""
    SELECT COUNT(n) FROM Note n WHERE n.topic.id = :topicId AND n.topic.user.id = :userId
    """)
    Long countByTopicIdAndTopicUserId(Long topicId, UUID userId);
}