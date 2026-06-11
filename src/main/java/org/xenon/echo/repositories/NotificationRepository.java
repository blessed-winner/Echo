package org.xenon.echo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.enums.NotificationStatus;

import java.time.Instant;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Long countByRecipientIdAndReadFalse(UUID userId);
    Page<Notification> findByStatusAndDeliverAtBefore(NotificationStatus status, Instant time, Pageable pageable);
}
