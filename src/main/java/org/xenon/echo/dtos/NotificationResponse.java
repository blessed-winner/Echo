package org.xenon.echo.dtos;


import org.xenon.echo.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean read;
    private Instant createdAt;
}
