package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.echo.dtos.*;
import org.xenon.echo.entities.Note;
import org.xenon.echo.entities.Tag;
import org.xenon.echo.exceptions.ForbiddenException;
import org.xenon.echo.mappers.MemoryItemMapper;
import org.xenon.echo.mappers.NoteMapper;
import org.xenon.echo.repositories.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public void addTagsToNote(Long id, Set<Long> tagIds) {
        UUID userId = getCurrentUser();

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note Not Found"));

        if (!note.getTopic().getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot access this note");
        }

        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new RuntimeException("Tag Not Found"));

            if (!tag.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Cannot add this tag to note");
            }

            note.getTags().add(tag);
        }

        noteRepository.save(note);
    }

    public void removeTagsFromNote(Long id, Set<Long> tagIds) {
        UUID userId = getCurrentUser();

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note Not Found"));

        if (!note.getTopic().getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot access this note");
        }

        Set<Tag> tagsToRemove = tagRepository.findAllById(tagIds)
                .stream()
                .peek(tag -> {
                    if (!tag.getUser().getId().equals(userId)) {
                        throw new ForbiddenException("Cannot remove this tag");
                    }
                })
                .collect(Collectors.toSet());

        note.getTags().removeAll(tagsToRemove);
        noteRepository.save(note);
    }
}
