package org.xenon.knowspace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.MemoryItem;

import java.util.List;
import java.util.UUID;

public interface MemoryItemRepository extends JpaRepository<MemoryItem,Long> {
    List<MemoryItem> findAllByUserId(UUID userId);
}
