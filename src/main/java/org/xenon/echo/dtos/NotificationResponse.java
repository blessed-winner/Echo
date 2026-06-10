package org.xenon.echo.dtos;

import lombok.*;
import org.xenon.echo.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean read;
    private Instant createdAt;
    private String referenceId;
}
