package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.echo.dtos.NotificationResponse;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.NotificationType;
import org.xenon.echo.repositories.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@AllArgsConstructor
public class NotificationService {
    private NotificationRepository notificationRepository;
    private SimpMessagingTemplate simpMessagingTemplate;
    public Notification createNotification(
            User recipient,
            String title,
            String message,
            NotificationType type
    ){

    }

    public void push(
            Notification notification
    ){
        NotificationResponse response = NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
    }
}
