package org.xenon.echo.services.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.enums.NotificationStatus;
import org.xenon.echo.repositories.NotificationRepository;
import org.xenon.echo.services.NotificationService;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 60000)
    public void processPendingNotifications(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification>notifications = notificationRepository.findByStatusAndDeliverAtBefore(
                NotificationStatus.PENDING,
                Instant.now(),
                pageable
        );

        notifications.forEach(notification -> {
            notificationService.push(notification);
            notification.setStatus(NotificationStatus.DELIVERED);
            notification.setDeliveredAt(Instant.now());
        });

        notificationRepository.saveAll(notifications);
    }
}
