package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.echo.dtos.NotificationRequest;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.services.NotificationService;

@Tag(name = "Notifications")
@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(
            NotificationRequest request
    ){
        return ResponseEntity.ok(notificationService.createNotification(request));
    }
}
