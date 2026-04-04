package org.xenon.knowspace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.Topic;

public interface TopicRepository extends JpaRepository<Topic,Long> {
}
