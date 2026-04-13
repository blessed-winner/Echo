package org.xenon.knowspace.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.Tag;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag,Long>{
    Optional<Tag> findByNameIgnoreCaseAndUserId(String name, UUID userId);
}
