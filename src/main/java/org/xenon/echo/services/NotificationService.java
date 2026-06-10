package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.dtos.NotificationRequest;
import org.xenon.echo.dtos.NotificationResponse;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.entities.User;
import org.xenon.echo.enums.NotificationStatus;
import org.xenon.echo.enums.NotificationType;
import org.xenon.echo.repositories.NotificationRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class NotificationService {
    private NotificationRepository notificationRepository;
    private SimpMessagingTemplate messagingTemplate;

    public UUID getCurrentUser(){
        return (UUID)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

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

    private void push(
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
                "/topic/users/" + notification.getRecipient().getId(),
                response
        );
    }

    @Transactional(readOnly = true)
    public long countUnread(){
        UUID userId = getCurrentUser();
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    public void markAsRead(UUID id){
        UUID userId = getCurrentUser();
        var notification = notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification not found"));
        if(notification.getRecipient().getId().equals(userId) && !notification.isRead()){
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse>getMyNotifications(int page,int size){
        UUID userId = getCurrentUser();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<Notification>myNotifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId,pageable);
        return myNotifications.map(notification -> {
            NotificationResponse response = NotificationResponse.builder()
                    .id(notification.getId())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .type(notification.getType())
                    .createdAt(notification.getCreatedAt())
                    .read(notification.isRead())
                    .build();

            return response;
        });
    }
}
