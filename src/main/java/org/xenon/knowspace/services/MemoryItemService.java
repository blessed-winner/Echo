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
import org.xenon.knowspace.entities.MemoryItem;
import org.xenon.knowspace.entities.Note;
import org.xenon.knowspace.entities.Tag;
import org.xenon.knowspace.entities.User;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.exceptions.MemoryItemNotFoundException;
import org.xenon.knowspace.exceptions.UserNotFoundException;
import org.xenon.knowspace.mappers.MemoryItemMapper;
import org.xenon.knowspace.repositories.MemoryItemRepository;
import org.xenon.knowspace.repositories.NoteRepository;
import org.xenon.knowspace.repositories.TagRepository;
import org.xenon.knowspace.repositories.UserRepository;

import java.security.Security;
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
    public MemoryItemDto createMemoryItem(MemoryItemRequest memoryItemRequest) {
         var authentication = SecurityContextHolder.getContext().getAuthentication();
         UUID userId = (UUID) authentication.getPrincipal();
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
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!memoryItem.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot get this memory item");
        }
        return memoryItemMapper.toDto(memoryItem);
    }

    public MemoryItemDto updateMemoryItem(Long id, MemoryItemRequest request){
        var memoryItem = memoryItemRepository.
    }
}
