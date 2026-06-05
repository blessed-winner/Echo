package org.xenon.echo.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String message;

    private NotificationType type;
}
