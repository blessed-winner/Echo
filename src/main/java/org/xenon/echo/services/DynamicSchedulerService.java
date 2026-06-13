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
        log.info("Rescheduling existing memory item reminders on startup...");
        
        // Find all memory items where nextReviewDate is after now
        var itemsToReschedule = memoryItemRepository.findAll()
                .stream()
                .filter(item -> item.getNextReviewDate() != null && item.getNextReviewDate().isAfter(LocalDateTime.now()))
                .toList();
                
        for (MemoryItem item : itemsToReschedule) {
            try {
                log.info("Rescheduling reminder for existing memory item: {}", item.getId());
                scheduleReminderForMemoryItem(item);
            } catch (Exception e) {
                log.error("Error rescheduling reminder for memory item {}", item.getId(), e);
            }
        }
        
        log.info("Done rescheduling {} reminders", itemsToReschedule.size());
    }

    // Helper method to schedule reminder
    public void scheduleReminderForMemoryItem(MemoryItem memoryItem) {
        String taskId = "memory-item-reminder-" + memoryItem.getId();
        cancelTask(taskId);

        if (memoryItem.getNextReviewDate() == null) {
            return;
        }

        LocalDateTime reminderDateTime = calculateReminderDateTime(memoryItem);
        log.info("Calculated reminder time: {}", reminderDateTime);
        if (reminderDateTime != null && reminderDateTime.isAfter(LocalDateTime.now())) {
            // Convert to Instant
            Instant reminderInstant = reminderDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
            log.info("Scheduling reminder for instant: {}", reminderInstant);
            this.scheduleOnce(
                taskId,
                () -> sendReminderNotification(memoryItem.getId()),
                reminderInstant
            );
        } else {
            log.info("Not scheduling reminder - date is null or in the past");
        }
    }

    // Helper method to calculate reminder time
    private LocalDateTime calculateReminderDateTime(MemoryItem memoryItem) {
        LocalDateTime nextReviewDate = memoryItem.getNextReviewDate();
        String customReminderTime = memoryItem.getCustomReminderTime();
        log.info("Calculating reminder time - customReminderTime: {}, nextReviewDate: {}", customReminderTime, nextReviewDate);

        if (nextReviewDate == null) {
            log.info("nextReviewDate is null, cannot calculate reminder time");
            return null;
        }

        if (customReminderTime != null && customReminderTime.matches("\\d{2}:\\d{2}")) {
            log.info("Custom reminder time matches pattern, parsing...");
            // Parse custom time (HH:mm)
            String[] parts = customReminderTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            
            LocalDateTime result = nextReviewDate.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            log.info("Parsed custom reminder time result: {}", result);
            
            // If the result is in the past, schedule for tomorrow at the same time instead!
            if (result.isBefore(LocalDateTime.now())) {
                result = LocalDateTime.now().plusDays(1).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
                log.info("Result was in past, rescheduling to tomorrow: {}", result);
            }
            
            return result;
        }

        log.info("Using default nextReviewDate");
        // Default: use nextReviewDate as is
        return nextReviewDate;
    }

    @Transactional
    public void sendReminderNotification(Long memoryItemId) {
        log.info("sendReminderNotification called for memory item ID: {}", memoryItemId);
        
        MemoryItem memoryItem = memoryItemRepository.findById(memoryItemId).orElse(null);
        if (memoryItem == null) {
            log.error("Memory item not found for ID: {}", memoryItemId);
            return;
        }
        if (memoryItem.getUser() == null) {
            log.error("Memory item has no user: {}", memoryItemId);
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

        log.info("Saving notification for user {}: {}", memoryItem.getUser().getId(), message);
        notificationRepository.save(notification);
        log.info("Pushing notification via notificationService...");
        notificationService.push(notification);
        log.info("Notification saved and pushed successfully!");
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
