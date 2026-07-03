package org.xenon.echo.services.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.entities.MemoryItem;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.enums.NotificationStatus;
import org.xenon.echo.enums.NotificationType;
import org.xenon.echo.repositories.MemoryItemRepository;
import org.xenon.echo.repositories.NotificationRepository;
import org.xenon.echo.services.NotificationService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewReminderScheduler {
    private final MemoryItemRepository memoryItemRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    @Transactional
    public void createReviewReminders() {
        
        // Find all memory items that are due or overdue
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourFromNow = now.plusHours(1);
        
        Pageable pageable = PageRequest.of(0, 100);
        Page<MemoryItem> dueItems = memoryItemRepository.findDueItems(now, pageable);
        
        int remindersCreated = 0;
        
        for (MemoryItem item : dueItems.getContent()) {
            LocalDateTime dayAgo = now.minusDays(1);
            Instant dayAgoInstant = Instant.now().minus(24, ChronoUnit.HOURS);
            
            boolean alreadyNotified = notificationRepository
                .existsByRecipientIdAndTypeAndCreatedAtAfter(
                    item.getUser().getId(),
                    NotificationType.MEMORY_REVIEW,
                    dayAgoInstant
                );
            
            if (!alreadyNotified && item.getUser() != null) {
                String title = "Time to Review!";
                String message = String.format(
                    "You have %d item(s) ready for review. Strengthen your memory now!",
                    dueItems.getTotalElements()
                );
                
                Notification notification = Notification.builder()
                    .recipient(item.getUser())
                    .title(title)
                    .message(message)
                    .type(NotificationType.MEMORY_REVIEW)
                    .status(NotificationStatus.DELIVERED)
                    .read(false)
                    .build();
                
                notificationRepository.save(notification);
                notificationService.push(notification);
                remindersCreated++;

                break;
            }
        }
        
        log.info("Review reminder check complete. Created {} reminders", remindersCreated);
    }
    @Scheduled(cron = "0 0 8 * * *") // Every day at 8:00 AM
    @Transactional
    public void sendDailySummary() {
        
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 1000);

        List<Object[]> userDueCounts = memoryItemRepository.countDueItemsByUser(now);
        
        int summariesSent = 0;
        
        for (Object[] result : userDueCounts) {
            var userId = result[0];
            Long dueCount = (Long) result[1];
            
            if (dueCount > 0) {
                List<MemoryItem> userItems = memoryItemRepository.findTopByUserIdOrderByNextReviewDateAsc(
                    (java.util.UUID) userId,
                    PageRequest.of(0, 1)
                ).getContent();
                
                if (!userItems.isEmpty()) {
                    MemoryItem sample = userItems.get(0);
                    
                    String title = "Daily Review Summary";
                    String message = String.format(
                        "Good morning! You have %d memory item(s) ready for review today. " +
                        "Let's maintain that streak! 🔥",
                        dueCount
                    );
                    
                    Notification notification = Notification.builder()
                        .recipient(sample.getUser())
                        .title(title)
                        .message(message)
                        .type(NotificationType.REMINDER)
                        .status(NotificationStatus.DELIVERED)
                        .read(false)
                        .build();
                    
                    notificationRepository.save(notification);
                    notificationService.push(notification);
                    summariesSent++;
                }
            }
        }
        
        log.info("Daily summary complete. Sent {} summaries", summariesSent);
    }
}
