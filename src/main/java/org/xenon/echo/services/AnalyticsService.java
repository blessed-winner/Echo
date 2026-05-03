package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;
import org.xenon.echo.dtos.AdminSystemAnalyticsDto;
import org.xenon.echo.dtos.UserAnalyticsDto;
import org.xenon.echo.entities.User;
import org.xenon.echo.repositories.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AnalyticsService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final AuthenticationManager authenticationManager;

    public AdminSystemAnalyticsDto getSystemAnalytics(){
        AdminSystemAnalyticsDto dto = new AdminSystemAnalyticsDto();
        dto.setTotalNotes(noteRepository.count());
        dto.setTotalUsers(userRepository.count());
        dto.setTotalMemoryItems(memoryItemRepository.count());
        dto.setTotalTags(tagRepository.count());
        dto.setTotalReviews(reviewRepository.count());

        return dto;
    }

    public UserAnalyticsDto getMyAnalytics(){
        UUID userId = getCurrentUser();
        long totalNotes = noteRepository.countByTopicUserId(userId);
        long totalItems = memoryItemRepository.countByUserId(userId);
        long dueItems = memoryItemRepository.countDueItems(userId, LocalDateTime.now());
        long totalReviews = reviewRepository.countByUserId(userId);
        long reviewsToday = reviewRepository.countToday(userId,LocalDateTime.now().toLocalDate().atStartOfDay());
        long reviewThisWeek = reviewRepository.countReviewsThisWeek(userId, LocalDateTime.now().with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay());
        double retentionRate = (reviewRepository.countSuccessfulReviews(userId)*100/totalReviews);
        int currentStreak = calculateStreak(userId);

        return new UserAnalyticsDto(
                totalNotes,
                totalItems,
                dueItems,
                totalReviews,
                reviewsToday,
                reviewThisWeek,
                retentionRate,
                currentStreak
        );
    }

    private int calculateStreak(UUID userId){
       List<LocalDate> reviewDates = reviewRepository.findDistinctReviewDatesByUserId(userId)
               .stream()
               .map(LocalDateTime::toLocalDate)
               .distinct()
               .toList();
       if(reviewDates.isEmpty()){
           return 0;
       }

        LocalDate today = LocalDate.now();
        LocalDate expectedDate = today;

        if(!reviewDates.contains(today)){
            expectedDate = today.minusDays(1);
        }

        int streak = 0;
        for(LocalDate date : reviewDates){
            if(date.equals(expectedDate)){
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else if(date.isBefore(expectedDate)){
                break;
            }
        }

        return streak;
    }

    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
