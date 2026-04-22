package org.xenon.echo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.echo.entities.AuditLog;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
