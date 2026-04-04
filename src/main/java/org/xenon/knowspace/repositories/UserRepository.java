package org.xenon.knowspace.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.knowspace.entities.User;

public interface UserRepository extends JpaRepository<User,String> {
}
