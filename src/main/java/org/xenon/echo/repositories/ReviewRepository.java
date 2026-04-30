package org.xenon.echo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.echo.entities.Review;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    Page<Review> findByMemoryItemIdAndMemoryItemUserId(Long memoryItemId, UUID userId, Pageable pageable);
}
