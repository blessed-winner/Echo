package org.xenon.knowspace.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.Note;

import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note,Long> {
    Page<Note> findAllByUserId(UUID userId, Pageable pageable);
}
