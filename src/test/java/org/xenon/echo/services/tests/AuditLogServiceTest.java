package org.xenon.echo.services.tests;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.xenon.echo.enums.AuditAction;
import org.xenon.echo.repositories.AuditLogRepository;
import org.xenon.echo.services.AuditLogService;

import java.util.UUID;

public class AuditLogServiceTest {
    @Test
    void shouldSaveAuditLogWithCorrectData(){
        AuditLogRepository repo = mock(AuditLogRepository.class);
        AuditLogService service = new AuditLogService(repo);

        UUID userId = UUID.randomUUID();
        AuditAction action = AuditAction.LOGIN_SUCCESS;
        String ip = "127.0.0.1";

        service.log(userId,action,ip,true,null,"meta");
        verify(repo).save(argThat(log->
            log.getUserId() == userId &&
            log.getAction() == action &&
            log.getIpAddress().equals(ip) &&
            log.isSuccess() &&
            log.getFailureReason() == null &&
            log.getMetadata().equals("meta")&&
            log.getTimestamp() != null
        ));
    }
}
