package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.dtos.*;
import org.xenon.echo.entities.*;
import org.xenon.echo.enums.RescheduleType;
import org.xenon.echo.enums.ReviewRating;
import org.xenon.echo.exceptions.ForbiddenException;
import org.xenon.echo.exceptions.MemoryItemNotFoundException;
import org.xenon.echo.exceptions.UserNotFoundException;
import org.xenon.echo.mappers.MemoryItemMapper;
import org.xenon.echo.repositories.*;
import org.xenon.echo.services.NotificationService;
import org.xenon.echo.entities.Notification;
import org.xenon.echo.enums.NotificationStatus;
import org.xenon.echo.enums.NotificationType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class MemoryItemService {
    private final MemoryItemRepository memoryItemRepository;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final MemoryItemMapper memoryItemMapper;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final DynamicSchedulerService dynamicSchedulerService;

    private UUID getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("User is not authenticated");
        }
        return (UUID) authentication.getPrincipal();
    }


    public MemoryItemDto createMemoryItem(MemoryItemRequest memoryItemRequest) {

         log.info("Creating memory item with customReminderTime: {}", memoryItemRequest.getCustomReminderTime());

         UUID userId = getCurrentUser();
         User currentUser = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));
         Note note = noteRepository.findById(memoryItemRequest.getNoteId()).orElseThrow();
         if(note.getTopic() == null || !note.getTopic().getUser().getId().equals(currentUser.getId())){
             throw new ForbiddenException("Cannot add memory to this note");
         }
         var memoryItem = memoryItemMapper.toEntity(memoryItemRequest);
         log.info("Mapped memory item has customReminderTime: {}", memoryItem.getCustomReminderTime());
         memoryItem.setUser(currentUser);
         memoryItem.setNote(note);
         memoryItem.setInterval(1);
         memoryItem.setEaseFactor(2.5F);
         memoryItem.setReviewCount(0);
         memoryItem.setNextReviewDate(LocalDateTime.now());
         memoryItem.setCreatedAt(LocalDateTime.now());
         memoryItemRepository.save(memoryItem);

         Set<Tag> tags = new HashSet<>();

         for (Long tagId:memoryItemRequest.getTagIds()){
             var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
             if(!tag.getUser().getId().equals(currentUser.getId())){
                    throw new ForbiddenException("Cannot add this tag to memory item");
             }
             tags.add(tag);
         }
         memoryItem.setTags(tags);
         memoryItemRepository.save(memoryItem); // Ensure tags are saved with the memory item
         dynamicSchedulerService.scheduleReminderForMemoryItem(memoryItem);
         return memoryItemMapper.toDto(memoryItem);
    }

    @Transactional(readOnly = true)
    public Page<MemoryItemDto> getAllMemoryItems(UUID userId,int page, int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("nextReviewDate").ascending());
        Page<MemoryItem> memoryItemsPage = memoryItemRepository.findAllByUserId(userId,pageable);
        return memoryItemsPage.map(memoryItemMapper::toDto);
    }

    @Transactional(readOnly = true)
    public MemoryItemDto getMemoryItem(Long memoryId){
        var memoryItem = memoryItemRepository.findById(memoryId).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
        UUID userId = getCurrentUser();
        if(!memoryItem.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot get this memory item");
        }
        return memoryItemMapper.toDto(memoryItem);
    }

    public MemoryItemDto updateMemoryItem(Long id, MemoryItemUpdateRequest request){
        log.info("Updating memory item {} with customReminderTime: {}", id, request.getCustomReminderTime());

        var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
        UUID userId = getCurrentUser();
        if(!memoryItem.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot update this memory item");
        }

        Set<Tag> tags = new HashSet<>();

        if(request.getFront() != null && !request.getFront().isBlank()){memoryItem.setFront(request.getFront());}
        if(request.getBack() != null && !request.getBack().isBlank()){memoryItem.setBack(request.getBack());}
        if(request.getSource() != null && !request.getSource().isBlank()){memoryItem.setSource(request.getSource());}
        if(request.getCustomReminderTime() != null){
            memoryItem.setCustomReminderTime(request.getCustomReminderTime());
        }
        if(request.getTagIds() != null && !request.getTagIds().isEmpty()){
            for (Long tagId:request.getTagIds()){
                var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
                if(!tag.getUser().getId().equals(userId)){
                    throw new ForbiddenException("Cannot add this tag to memory item");
                }
                tags.add(tag);
            }
            memoryItem.setTags(tags);
        }

        dynamicSchedulerService.scheduleReminderForMemoryItem(memoryItem);
        return memoryItemMapper.toDto(memoryItem);
    }

    public void deleteMemoryItem(Long id){
        UUID userId = getCurrentUser();
        var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
        if(!memoryItem.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot delete this memory item");
    }
        memoryItemRepository.delete(memoryItem);
    }

    public MemoryItemDto review(Long id, ReviewRating rating, long timeSpentSeconds){
         UUID userId = getCurrentUser();
         var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
         if(!memoryItem.getUser().getId().equals(userId)){
                throw new ForbiddenException("Cannot review this memory item");
         }

         Review review = new Review();
         review.setMemoryItem(memoryItem);
         review.setReviewDate(LocalDateTime.now());
         review.setRating(rating);
         review.setIntervalBeforeReview(memoryItem.getInterval());
         review.setEaseFactorBefore(memoryItem.getEaseFactor());
         review.setTimeSpentSeconds(timeSpentSeconds);

         reviewRepository.save(review);

         applyReviewLogic(memoryItem,rating);
         dynamicSchedulerService.scheduleReminderForMemoryItem(memoryItem);

         return memoryItemMapper.toDto(memoryItem);
    }

    @Transactional(readOnly = true)
    public Page<MemoryItemDto> getDueMemoryItems(int limit, Long tagId){
        int safeLimit = Math.min(limit,50);
        UUID userId = getCurrentUser();
        Pageable pageable = PageRequest.of(0, safeLimit, Sort.by("nextReviewDate").ascending());
        Page<MemoryItem> memoryItemsPage;
        if(tagId != null){
            memoryItemsPage = memoryItemRepository.findByUserIdAndTagsIdAndNextReviewDateLessThanEqual(userId,tagId, LocalDateTime.now(), pageable);
        } else {
            memoryItemsPage = memoryItemRepository.findByUserIdAndNextReviewDateLessThanEqual(userId, LocalDateTime.now(), pageable);
        }

        return memoryItemsPage.map(memoryItemMapper::toDto);

    }

    private void applyReviewLogic(MemoryItem item, ReviewRating rating){
        int interval = item.getInterval();
        double easeFactor = item.getEaseFactor();
        switch (rating){
            case AGAIN -> {
                interval = 1;
                item.setReviewCount(0);
                easeFactor = Math.max(1.3,easeFactor - 0.2);
            }
            case HARD -> {
                interval = Math.max(1,(int)(interval*1.2));
                easeFactor = Math.max(1.3,easeFactor - 0.15);
                item.setReviewCount(item.getReviewCount() + 1);
            }

            case GOOD -> {
                interval = (int)(interval * easeFactor);
                item.setReviewCount(item.getReviewCount() + 1);
            }

            case EASY -> {
                interval = (int) (interval * easeFactor * 1.3);
                easeFactor += 0.1;
                item.setReviewCount(item.getReviewCount() + 1);
            }
        }

        item.setInterval(interval);
        item.setEaseFactor(easeFactor);
        item.setNextReviewDate(LocalDateTime.now().plusDays(interval));
        item.setLastReviewed(LocalDateTime.now());
    }

    public ReviewIntervalsDto calculatePreviewIntervals(Long memoryItemId) {
        MemoryItem item = memoryItemRepository.findById(memoryItemId)
                .orElseThrow(() -> new MemoryItemNotFoundException("Memory Item Not Found"));

        int currentInterval = item.getInterval();
        double currentEaseFactor = item.getEaseFactor();

        int againDays = 1;
        int hardDays = Math.max(1, (int)(currentInterval * 1.2));
        int goodDays = (int)(currentInterval * currentEaseFactor);
        int easyDays = (int)(currentInterval * currentEaseFactor * 1.3);

        return new ReviewIntervalsDto(againDays, hardDays, goodDays, easyDays);
    }

    @Transactional(readOnly = true)
    public MemoryStatsDto getStats(){
        UUID userId = getCurrentUser();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        var user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));

        long todayReviewed = memoryItemRepository.countReviewedToday(userId,todayStart,todayEnd);
        long upcoming = memoryItemRepository.countUpcoming(userId, LocalDateTime.now());
        long overdue = memoryItemRepository.countOverdue(userId, LocalDateTime.now());
        int streak = calculateStreak(user);

        MemoryStatsDto dto = new MemoryStatsDto();
        dto.setTodayReviewed(todayReviewed);
        dto.setOverdue(overdue);
        dto.setStreak(streak);
        dto.setUpcoming(upcoming);

        return dto;
    }


    private int calculateStreak(User user){
        LocalDate today = LocalDate.now();
        int streak = 0;
        List<LocalDate> reviewDates = memoryItemRepository.findLastReviewedDates(user.getId()).stream().map(LocalDateTime::toLocalDate).toList();
        for(LocalDate date : reviewDates){
            if(!today.isBefore(date.minusDays(streak))){
                streak++;
            }else{
                break;
            }
        }
        return streak;
    }

    public void reschedule(Long id, RescheduleType type){
       UUID userId = getCurrentUser();
       var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
       if(!memoryItem.getNote().getTopic().getUser().getId().equals(userId)){
           throw new ForbiddenException("Cannot access this item");
       }
       LocalDateTime now = LocalDateTime.now();
       LocalDateTime newDate = switch(type){
           case IN_1_HOUR -> now.plusHours(1);
           case IN_3_HOURS -> now.plusHours(3);
           case IN_1_DAY -> now.plusDays(1);
           case IN_3_DAYS -> now.plusDays(3);
           case IN_1_WEEK ->  now.plusDays(7);
       };

       memoryItem.setNextReviewDate(newDate);
       scheduleReminderForMemoryItem(memoryItem);

       String title = "Item Rescheduled!";
       String message = String.format(
           "Your memory item \"%s\" has been rescheduled to %s.",
           memoryItem.getFront(),
           newDate.toLocalDate()
       );

       Notification notification = Notification.builder()
           .recipient(memoryItem.getUser())
           .title(title)
           .message(message)
           .type(NotificationType.RESCHEDULE)
           .status(NotificationStatus.DELIVERED)
           .read(false)
           .build();

       notificationRepository.save(notification);
       notificationService.push(notification);
    }

    public void scheduleReminderForMemoryItem(MemoryItem memoryItem) {
        log.info("Scheduling reminder for memory item {} - customReminderTime: {}, nextReviewDate: {}",
                 memoryItem.getId(), memoryItem.getCustomReminderTime(), memoryItem.getNextReviewDate());

        String taskId = "memory-item-reminder-" + memoryItem.getId();
        dynamicSchedulerService.cancelTask(taskId);

        if (memoryItem.getNextReviewDate() == null) {
            log.info("Not scheduling reminder - nextReviewDate is null");
            return;
        }

        LocalDateTime reminderDateTime = calculateReminderDateTime(memoryItem);
        log.info("Calculated reminder date time: {}", reminderDateTime);
        if (reminderDateTime != null && reminderDateTime.isAfter(LocalDateTime.now())) {
            // Convert to Instant
            Instant reminderInstant = reminderDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
            log.info("Scheduling reminder for instant: {}", reminderInstant);
            dynamicSchedulerService.scheduleOnce(
                taskId,
                () -> sendReminderNotification(memoryItem.getId()),
                reminderInstant
            );
        } else {
            log.info("Not scheduling reminder - date is null or in the past");
        }
    }

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

    @org.springframework.transaction.annotation.Transactional
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
}
