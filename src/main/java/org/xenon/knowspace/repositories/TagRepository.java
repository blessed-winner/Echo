package org.xenon.knowspace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.Tag;

public interface TagRepository extends JpaRepository<Tag,Long>{
}
