package org.xenon.echo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.xenon.echo.entities.MemoryItem;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.enums.NotificationType;
import org.xenon.echo.enums.NotificationStatus;
import org.xenon.echo.repositories.MemoryItemRepository;
import org.xenon.echo.repositories.NotificationRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class DynamicSchedulerService {
    private final TaskScheduler taskScheduler;
    private final MemoryItemRepository memoryItemRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DynamicSchedulerService(TaskScheduler taskScheduler, MemoryItemRepository memoryItemRepository, NotificationRepository notificationRepository, NotificationService notificationService) {
        this.taskScheduler = taskScheduler;
        this.memoryItemRepository = memoryItemRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void rescheduleExistingReminders() {
        var itemsToReschedule = memoryItemRepository.findAll()
                .stream()
                .filter(item -> item.getNextReviewDate() != null && item.getNextReviewDate().isAfter(LocalDateTime.now()))
                .toList();
                
        for (MemoryItem item : itemsToReschedule) {
            try {
                scheduleReminderForMemoryItem(item);
            } catch (Exception e) {
                log.error("Error rescheduling reminder for memory item {}", item.getId(), e);
            }
        }
        
        log.info("Startup: rescheduled {} pending reminders", itemsToReschedule.size());
    }

    // Helper method to schedule reminder
    public void scheduleReminderForMemoryItem(MemoryItem memoryItem) {
        String taskId = "memory-item-reminder-" + memoryItem.getId();
        cancelTask(taskId);

        if (memoryItem.getNextReviewDate() == null) {
            return;
        }

        LocalDateTime reminderDateTime = calculateReminderDateTime(memoryItem);
        if (reminderDateTime != null && reminderDateTime.isAfter(LocalDateTime.now())) {
            Instant reminderInstant = reminderDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
            this.scheduleOnce(
                taskId,
                () -> sendReminderNotification(memoryItem.getId()),
                reminderInstant
            );
        }
    }

    // Helper method to calculate reminder time
    private LocalDateTime calculateReminderDateTime(MemoryItem memoryItem) {
        LocalDateTime nextReviewDate = memoryItem.getNextReviewDate();
        String customReminderTime = memoryItem.getCustomReminderTime();

        if (nextReviewDate == null) {
            return null;
        }

        if (customReminderTime != null && customReminderTime.matches("\\d{2}:\\d{2}")) {
            String[] parts = customReminderTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            LocalDateTime result = nextReviewDate.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            
            // If the result is in the past, schedule for tomorrow at the same time instead!
            if (result.isBefore(LocalDateTime.now())) {
                result = LocalDateTime.now().plusDays(1).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            }
            
            return result;
        }

        // Default: use nextReviewDate as is
        return nextReviewDate;
    }

    @Transactional
    public void sendReminderNotification(Long memoryItemId) {
        MemoryItem memoryItem = memoryItemRepository.findById(memoryItemId).orElse(null);
        if (memoryItem == null) {
            log.error("Memory item not found for scheduled reminder: id={}", memoryItemId);
            return;
        }
        if (memoryItem.getUser() == null) {
            log.error("Memory item {} has no associated user — skipping notification", memoryItemId);
            return;
        }

        String title = "Time to Review!";
        String message = String.format(
            "Don't forget to review your memory item: \"%s\"",
            memoryItem.getFront()
        );

        Notification notification = Notification.builder()
            .recipient(memoryItem.getUser())
            .title(title)
            .message(message)
            .type(NotificationType.MEMORY_REVIEW)
            .status(NotificationStatus.DELIVERED)
            .read(false)
            .referenceId(String.valueOf(memoryItemId))
            .build();

        notificationRepository.save(notification);
        notificationService.push(notification);
        log.info("Reminder notification dispatched for memory item {} (user {})", memoryItemId, memoryItem.getUser().getId());
    }

    public void scheduleOnce(String taskId, Runnable task, Instant runAt) {
        cancelTask(taskId);
        if (runAt.isAfter(Instant.now())) {
            ScheduledFuture<?> future = taskScheduler.schedule(task, runAt);
            scheduledTasks.put(taskId, future);
        }
    }

    public void scheduleCron(String taskId, Runnable task, String cronExpression) {
        cancelTask(taskId);
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(cronExpression));
        scheduledTasks.put(taskId, future);
    }

    public void scheduleFixedRate(String taskId, Runnable task, long fixedRateMs) {
        cancelTask(taskId);
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, fixedRateMs);
        scheduledTasks.put(taskId, future);
    }

    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
