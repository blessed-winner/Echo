package org.xenon.echo.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.services.NotificationService;

@RestController
@AllArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    public Notification createNotification(){

    }
}
