package org.xenon.echo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.echo.entities.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<Long, VerificationToken> {
}
