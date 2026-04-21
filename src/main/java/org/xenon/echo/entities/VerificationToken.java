package org.xenon.echo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.enums.TokenType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "verification_tokens",
        indexes = {
                @Index(name = "idx_token_hash", columnList = "tokenHash"),
                @Index(name = "idx_user_type", columnList = "userId, type"),
                @Index(name = "idx_expires_at", columnList = "expiresAt")
        }
)
@Getter
@Setter
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;
}
