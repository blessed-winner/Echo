package org.xenon.echo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.echo.enums.Role;
import org.xenon.echo.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    List<User> findByRoleAndIdNot(Role role, UUID excludeUserId);

    List<User> findAllByIdNot(UUID excludeUserId);
}
