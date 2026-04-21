package org.xenon.echo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.echo.entities.VerificationToken;
import org.xenon.echo.enums.TokenType;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<UUID, VerificationToken> {
    Optional<VerificationToken> findByTokenHash(String tokenHash);
    void deleteByUserIdAndType(UUID userId, TokenType type);
    void deleteByExpiresAtBefore(LocalDateTime now);
}
