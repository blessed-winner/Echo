package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.*;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.mappers.NoteMapper;
import org.xenon.knowspace.mappers.TagMapper;
import org.xenon.knowspace.repositories.MemoryItemRepository;
import org.xenon.knowspace.repositories.NoteRepository;
import org.xenon.knowspace.repositories.TagRepository;
import org.xenon.knowspace.repositories.UserRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final NoteMapper noteMapper;

    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public TagDto createTag(TagRequest tagRequest) {
        UUID userId = getCurrentUser();
        var user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        var existingTag = tagRepository.findByNameIgnoreCaseAndUserId(tagRequest.getName(), userId);
        if(existingTag.isPresent()){
            throw new ForbiddenException("Tag with the same name already exists");
        }
        var tag = tagMapper.toEntity(tagRequest);
        tag.setUser(user);

        return tagMapper.toDto(tagRepository.save(tag));
    }

    public Set<TagResponseDto> getAllTags(){
        UUID userId = getCurrentUser();
        var tags = tagRepository.findByUserId(userId);

        return tags.stream().map(tag -> {
            var tagResponseDto = new TagResponseDto();
            tagResponseDto.setId(tag.getId());
            tagResponseDto.setName(tag.getName());
            tagResponseDto.setCount(tagRepository.countTagsByUserId(userId, tag.getId()));
            return tagResponseDto;
        }).collect(Collectors.toSet());
    }

    public TagDto updateTag(Long tagId, TagRequest tagRequest){
        UUID userId = getCurrentUser();
        var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
        if(!tag.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot update this tag");
        }
        tag.setName(tagRequest.getName());
        return tagMapper.toDto(tagRepository.save(tag));
    }

    public TagSummaryDto getTagSummary(Long tagId){
        UUID userId = getCurrentUser();
        var tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
        if(!tag.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this tag");
        }
        var tagSummaryDto = new TagSummaryDto();
        tagSummaryDto.setId(tag.getId());
        tagSummaryDto.setName(tag.getName());
        tagSummaryDto.setTotalNotes(noteRepository.countNotesByTag(tag.getId(), userId));
        tagSummaryDto.setTotalItems(memoryItemRepository.countMemoryItemsByTag(tag.getId(), userId));

        return tagSummaryDto;
    }

    public void deleteTag(Long tagId) {
        UUID userId = getCurrentUser();
        var tag = tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("Tag Not Found"));
        if (!tag.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot delete this tag");
        }
        tagRepository.delete(tag);
    }

    public Page<NoteDto> getNotesByTag(Long tagId, int page, int size){
        UUID userId = getCurrentUser();
        var tag = tagRepository.findById(tagId).orElseThrow(() -> new RuntimeException("Tag Not Found"));
        if (!tag.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot access this tag");
        }
        Pageable pageable = PageRequest.of(page, size);
        var notesPage = noteRepository.findByTagsIdAndTopicUserId(tagId, userId, pageable);
        return notesPage.map(noteMapper::toDto);
    }
}

