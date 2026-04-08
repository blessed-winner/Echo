package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.entities.MemoryItem;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
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
        dto.setTags(memoryItem.getTags());
        return dto;
    }
    MemoryItem toEntity(MemoryItemRequest request);
}
