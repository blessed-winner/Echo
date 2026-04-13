package org.xenon.knowspace.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.xenon.knowspace.entities.Tag;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag,Long>{
    Optional<Tag> findByNameIgnoreCaseAndUserId(String name, UUID userId);

    Set<Tag> findByUserId(UUID userId);
    @Query("""
      SELECT COUNT(t) FROM Tag t WHERE t.id = :tagId AND t.user.id = :userId
    """)
    Integer countTagsByUserId(UUID userId, Long tagId);
}
