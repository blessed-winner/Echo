package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.*;
import org.xenon.knowspace.entities.Note;
import org.xenon.knowspace.entities.Tag;
import org.xenon.knowspace.entities.User;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.exceptions.UserNotFoundException;
import org.xenon.knowspace.mappers.MemoryItemMapper;
import org.xenon.knowspace.mappers.NoteMapper;
import org.xenon.knowspace.repositories.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NoteService {
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;
    private final TopicRepository topicRepository;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final MemoryItemMapper memoryItemMapper;

    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public NoteDto createNote(NoteRequest noteRequest){
        UUID userId = getCurrentUser();
        var topic = topicRepository.findById(noteRequest.getTopicId()).orElseThrow(()->new RuntimeException("Topic Not Found"));
        if(!topic.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot add note to this topic");
        }
        Note note = noteMapper.toEntity(noteRequest);
        note.setTopic(topic);
        note.setCreatedAt(LocalDateTime.now());
        Set<Tag> tags = new HashSet<>();
        if(noteRequest.getTagIds() != null) {
            for (Long tagId : noteRequest.getTagIds()) {
                Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("Tag Not Found"));
                if (!tag.getUser().getId().equals(userId)) {
                    throw new ForbiddenException("Cannot add this tag to note" + tag.getName());
                }
                tags.add(tag);
            }
        }

        note.setTags(tags);
        noteRepository.save(note);

        return noteMapper.toDto(note);

    }

    public Page<NoteDto> getAllNotes(int page, int size){
        UUID userId = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var notesPage = noteRepository.findAllByTopicUserId(userId, pageable);
        return notesPage.map(noteMapper::toDto);
    }

    public NoteDto getNote(Long id){
        UUID userId = getCurrentUser();
        var note = noteRepository.findById(id).orElseThrow(()->new RuntimeException("Note Not Found"));
        if(!note.getTopic().getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this note");
        }
        return noteMapper.toDto(note);
    }

    public NoteDto updateNote(Long id, NoteUpdateRequest request){
        UUID userId = getCurrentUser();
        var note = noteRepository.findById(id).orElseThrow(()->new RuntimeException("Note Not Found"));
        if(!note.getTopic().getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this note");
        }

        Set<Tag> tags = new HashSet<>();
        if(request.getTitle() != null){note.setTitle(request.getTitle());}
        if(request.getContent() != null){note.setContent(request.getContent());}
        if(request.getTagIds() != null){
            for(Long tagId : request.getTagIds()){
                var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
                if(!tag.getUser().getId().equals(userId)){
                    throw new ForbiddenException("Cannot update note with this tag");
                }

                tags.add(tag);
            }

            note.setTags(tags);
        }

        return noteMapper.toDto(noteRepository.save(note));
    }

    public void deleteNote(Long id){
        UUID userId = getCurrentUser();
        var note = noteRepository.findById(id).orElseThrow(()->new RuntimeException("Note Not Found"));
        if(!note.getTopic().getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot delete this note");
        }
        noteRepository.delete(note);
    }

    public NoteSummaryDto getNoteSummary(Long id){
        UUID userId = getCurrentUser();
        var note = noteRepository.findById(id).orElseThrow(()->new RuntimeException("Note Not Found"));
        long totalItems = memoryItemRepository.countAllItemsByNoteIdAndUserId(note.getId(), userId);
        long dueItems = memoryItemRepository.countDueItemsByNoteIdAndUserId(note.getId(), userId, LocalDateTime.now());
        long reviewedToday = memoryItemRepository.countReviewedTodayByNoteIdAndUserId(note.getId(), userId, LocalDateTime.now());

        NoteSummaryDto dto = new NoteSummaryDto();
        dto.setTotalItems(totalItems);
        dto.setDueItems(dueItems);
        dto.setReviewedToday(reviewedToday);

        return dto;
    }

    public Page<MemoryItemDto> getDueMemoryItemsPerNote(Long id, int page, int size){
        UUID userId = getCurrentUser();
        var note = noteRepository.findById(id).orElseThrow(()->new RuntimeException("Note Not Found"));
        if(!note.getTopic().getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this note");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("nextReviewDate").ascending());
        var memoryItemsPage = memoryItemRepository.findByNoteIdAndNoteTopicUserIdAndNextReviewDateLessThanEqual(note.getId(), userId, LocalDateTime.now(), pageable);
        return memoryItemsPage.map(memoryItemMapper::toDto);
    }

    public Page<NoteDto> searchNotes(String query, int page, int size){
        UUID userId = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var notesPage = noteRepository.findByTitleContainingIgnoreCaseAndTopicUserId(query, userId, pageable);
        return notesPage.map(noteMapper::toDto);
    }

    public void addTagsToNote(Long id, Set<Long> tagIds){
        UUID userId = getCurrentUser();
        var note = noteRepository.findById(id).orElseThrow(()->new RuntimeException("Note Not Found"));
        if(!note.getTopic().getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this note");
        }
        Set<Tag> tags = note.getTags();
        for(Long tagId : tagIds){
            var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
            if(!tag.getUser().getId().equals(userId)){
                throw new ForbiddenException("Cannot add this tag to note" + tag.getName());
            }
            tags.add(tag);
        }
        note.getTags().addAll(tags);
        noteRepository.save(note);
    }

    public void removeTagsFromNote(Long id, Set<Long> tagIds){
        UUID userId = getCurrentUser();
        var note = noteRepository.findById(id).orElseThrow(()->new RuntimeException("Note Not Found"));
        if(!note.getTopic().getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this note");
        }
        Set<Tag> tags = note.getTags();
        for(Long tagId : tagIds){
            var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
            if(!tag.getUser().getId().equals(userId)){
                throw new ForbiddenException("Cannot remove this tag from note" + tag.getName());
            }
            tags.remove(tag);
        }
        note.getTags().removeAll(tags);
        noteRepository.save(note);
    }
}
