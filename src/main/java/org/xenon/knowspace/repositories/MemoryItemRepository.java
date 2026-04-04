package org.xenon.knowspace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.MemoryItem;

public interface MemoryItemRepository extends JpaRepository<MemoryItem,Long> {
}
