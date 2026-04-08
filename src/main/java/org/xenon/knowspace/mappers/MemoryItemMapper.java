package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.dtos.TagDto;
import org.xenon.knowspace.entities.MemoryItem;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface MemoryItemMapper {
    default MemoryItemDto toDto(MemoryItem memoryItem){
        MemoryItemDto dto = new MemoryItemDto();
        dto.setId(memoryItem.getId());
        dto.setText(memoryItem.getText());
        dto.setSource(memoryItem.getSource());
        dto.setCreatedAt(LocalDateTime.now());
        dto.setLastReviewed(memoryItem.getLastReviewed());
        dto.setNextReviewDate(memoryItem.getNextReviewDate());
        dto.setInterval(memoryItem.getInterval());
        dto.setEaseFactor(memoryItem.getEaseFactor());
        dto.setReviewCount(memoryItem.getReviewCount());
        dto.setInterval(memoryItem.getInterval());
        dto.setDue(memoryItem.getNextReviewDate().isBefore(LocalDateTime.now()));
        dto.setNoteId(memoryItem.getNote().getId());

        Set<TagDto> tagDtos = memoryItem.getTags().stream()
                        .map(tag -> new TagDto(tag.getId(), tag.getName()))
                        .collect(Collectors.toSet());

        dto.setTags(tagDtos);
        return dto;
    }
    MemoryItem toEntity(MemoryItemRequest request);
}
