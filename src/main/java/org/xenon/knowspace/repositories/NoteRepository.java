package org.xenon.knowspace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.Note;

public interface NoteRepository extends JpaRepository<Note,Long> {
}
