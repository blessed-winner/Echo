package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.dtos.MemoryItemUpdateRequest;
import org.xenon.knowspace.dtos.MemoryStatsDto;
import org.xenon.knowspace.entities.*;
import org.xenon.knowspace.enums.ReviewRating;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.exceptions.MemoryItemNotFoundException;
import org.xenon.knowspace.exceptions.UserNotFoundException;
import org.xenon.knowspace.mappers.MemoryItemMapper;
import org.xenon.knowspace.repositories.MemoryItemRepository;
import org.xenon.knowspace.repositories.NoteRepository;
import org.xenon.knowspace.repositories.TagRepository;
import org.xenon.knowspace.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MemoryItemService {
    private final MemoryItemRepository memoryItemRepository;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final MemoryItemMapper memoryItemMapper;
    private final TagRepository tagRepository;

    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    public MemoryItemDto createMemoryItem(MemoryItemRequest memoryItemRequest) {

         UUID userId = getCurrentUser();
         User currentUser = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User Not Found"));
         Note note = noteRepository.findById(memoryItemRequest.getNoteId()).orElseThrow();
         if(note.getTopic() == null || !note.getTopic().getUser().getId().equals(currentUser.getId())){
             throw new ForbiddenException("Cannot add memory to this note");
         }
         var memoryItem = memoryItemMapper.toEntity(memoryItemRequest);
         memoryItem.setUser(currentUser);
         memoryItem.setNote(note);
         memoryItem.setInterval(1);
         memoryItem.setEaseFactor(2.5F);
         memoryItem.setReviewCount(0);
         memoryItem.setNextReviewDate(LocalDateTime.now());

         Set<Tag> tags = new HashSet<>();

         for (Long tagId:memoryItemRequest.getTagIds()){
             var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
             if(!tag.getUser().getId().equals(currentUser.getId())){
                    throw new ForbiddenException("Cannot add this tag to memory item");
             }
             tags.add(tag);
         }

         memoryItemRepository.save(memoryItem);

         return memoryItemMapper.toDto(memoryItem);
    }

    public Page<MemoryItemDto> getAllMemoryItems(UUID userId,int page, int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("nextReviewDate").ascending());
        Page<MemoryItem> memoryItemsPage = memoryItemRepository.findAllByUserId(userId,pageable);
        return memoryItemsPage.map(memoryItemMapper::toDto);
    }

    public MemoryItemDto getMemoryItem(Long memoryId){
        var memoryItem = memoryItemRepository.findById(memoryId).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
        UUID userId = getCurrentUser();
        if(!memoryItem.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot get this memory item");
        }
        return memoryItemMapper.toDto(memoryItem);
    }

    public MemoryItemDto updateMemoryItem(Long id, MemoryItemUpdateRequest request){
        var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
        UUID userId = getCurrentUser();
        if(!memoryItem.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot update this memory item");
        }

        Set<Tag> tags = new HashSet<>();

        if(request.getText() != null && !request.getText().isBlank()){memoryItem.setText(request.getText());}
        if(request.getSource() != null && !request.getSource().isBlank()){memoryItem.setSource(request.getSource());}
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
        memoryItemRepository.save(memoryItem);

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

    public MemoryItemDto review(Long id, ReviewRating rating){
         UUID userId = getCurrentUser();
         var memoryItem = memoryItemRepository.findById(id).orElseThrow(()->new MemoryItemNotFoundException("Memory Item Not Found"));
         if(!memoryItem.getUser().getId().equals(userId)){
                throw new ForbiddenException("Cannot review this memory item");
         }

         applyReviewLogic(memoryItem,rating);

         memoryItemRepository.save(memoryItem);

         return memoryItemMapper.toDto(memoryItem);
    }

    public Page<MemoryItemDto> getDueMemoryItems(int limit, Long tagId){
        int safeLimit = Math.min(limit,50);
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(0, safeLimit, Sort.by("nextReviewDate").ascending());
        Page<MemoryItem> memoryItemsPage;
        if(tagId != null){
            memoryItemsPage = memoryItemRepository.findByUserIdAndTagIdAndNextReviewDateLessThanEqual(userId,tagId, LocalDateTime.now(), pageable);
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
        List<LocalDate> reviewDates = memoryItemRepository.findLastReviewedDates(user.getId());
        for(LocalDate date : reviewDates){
            if(!today.isBefore(date.minusDays(streak))){
                streak++;
            }else{
                break;
            }
        }
        return streak;
    }
}
