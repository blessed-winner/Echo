package org.xenon.echo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.enums.AuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    private boolean success;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(length = 500)
    private String metadata;

    private LocalDateTime timestamp;
}
