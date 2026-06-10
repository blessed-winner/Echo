package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.echo.dtos.NotificationRequest;
import org.xenon.echo.dtos.NotificationResponse;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.NotificationStatus;
import org.xenon.echo.enums.NotificationType;
import org.xenon.echo.repositories.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@AllArgsConstructor
public class NotificationService {
    private NotificationRepository notificationRepository;
    private SimpMessagingTemplate messagingTemplate;
    public Notification createNotification(
            NotificationRequest request
    ){
        Notification notification = Notification.builder()
                .recipient(request.getRecipient())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .status(NotificationStatus.SENT)
                .read(false)
                .build();

        var saved = notificationRepository.save(notification);
        push(saved);
        return saved;

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
                .read(notification.isRead())
                .build();

        messagingTemplate.convertAndSend(
                "topic/users/" + notification.getRecipient().getId(),
                response
        );
    }
}
