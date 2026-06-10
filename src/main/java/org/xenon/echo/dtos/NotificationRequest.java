package org.xenon.echo.dtos;

import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.NotificationType;

@Getter
@Setter
public class NotificationRequest {
    private User recipient;
    private String title;
    private String message;
    private NotificationType type;
}
