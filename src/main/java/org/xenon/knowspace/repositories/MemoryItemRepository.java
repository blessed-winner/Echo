package org.xenon.knowspace.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.MemoryItem;

import java.util.UUID;

public interface MemoryItemRepository extends JpaRepository<MemoryItem,Long> {
    Page<MemoryItem> findAllByUserId(UUID userId, Pageable pageable);
}
