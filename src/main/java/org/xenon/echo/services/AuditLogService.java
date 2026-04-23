package org.xenon.echo.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.echo.entities.AuditLog;
import org.xenon.echo.enums.AuditAction;
import org.xenon.echo.repositories.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    public void log(UUID userId, AuditAction action, String ip,boolean success, String failureReason, String metadata){
        var auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setIpAddress(ip);
        auditLog.setSuccess(success);
        auditLog.setFailureReason(failureReason);
        auditLog.setMetadata(metadata);
        auditLog.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }
}
