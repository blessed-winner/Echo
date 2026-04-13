package org.xenon.knowspace.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.Topic;

import java.util.Optional;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic,Long> {
    Optional<Topic> findByNameIgnoreCaseAndUserId(String name, UUID user_id);
    Page<Topic> findByUserId(UUID userId, Pageable pageable);

    Page<Topic> findByNameContainingIgnoreCaseAndUserId(String query, UUID userId, Pageable pageable);
}
