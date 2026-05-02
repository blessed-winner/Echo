package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xenon.echo.dtos.AdminSystemAnalyticsDto;
import org.xenon.echo.repositories.*;

@Service
@AllArgsConstructor
public class AnalyticsService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    public AdminSystemAnalyticsDto getSystemAnalytics(){
        AdminSystemAnalyticsDto dto = new AdminSystemAnalyticsDto();
        dto.setTotalNotes(noteRepository.count());
        dto.setTotalUsers(userRepository.count());
        dto.setTotalMemoryItems(memoryItemRepository.count());
        dto.setTotalTags(tagRepository.count());
        dto.setTotalReviews(reviewRepository.count());

        return dto;
    }
}
