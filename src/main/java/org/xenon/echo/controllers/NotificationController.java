package org.xenon.echo.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.echo.dtos.NotificationRequest;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.services.NotificationService;

@RestController
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    public ResponseEntity<Notification> createNotification(
            NotificationRequest request
    ){
        return ResponseEntity.ok(notificationService.createNotification(request));
    }
}
