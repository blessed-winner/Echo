package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.entities.Note;
import org.xenon.knowspace.entities.Tag;
import org.xenon.knowspace.entities.User;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.exceptions.UserNotFoundException;
import org.xenon.knowspace.mappers.MemoryItemMapper;
import org.xenon.knowspace.repositories.MemoryItemRepository;
import org.xenon.knowspace.repositories.NoteRepository;
import org.xenon.knowspace.repositories.TagRepository;
import org.xenon.knowspace.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
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
         memoryItem.setNote(note);
         memoryItem.setInterval(1);
         memoryItem.setEaseFactor(2.5F);
         memoryItem.setReviewCount(0);
         memoryItem.setNextReviewDate(LocalDateTime.now());
         memoryItemRepository.save(memoryItem);

        Set<Tag> tags = new HashSet<>();

         for (Long tagId:memoryItemRequest.getTagIds()){
             var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
             if(!tag.getUser().equals(currentUser)){
                    throw new ForbiddenException("Cannot add this tag to memory item");
             }
             tags.add(tag);
         }

         memoryItemRepository.save(memoryItem);

         return memoryItemMapper.toDto(memoryItem);
    }
}
