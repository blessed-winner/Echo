package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xenon.echo.dtos.NotificationRequest;
import org.xenon.echo.dtos.NotificationResponse;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.services.NotificationService;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Tag(name = "Notifications")
@RestController
@AllArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody NotificationRequest request
    ){
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>>findMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(notificationService.getMyNotifications(page,size));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> countUnread(){
        return ResponseEntity.ok(notificationService.countUnread());
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void>markAsRead(@PathVariable UUID id) throws AccessDeniedException {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
