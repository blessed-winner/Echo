package org.xenon.knowspace.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.dtos.TagDto;
import org.xenon.knowspace.entities.MemoryItem;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface MemoryItemMapper {
    MemoryItemDto toDto(MemoryItem memoryItem);
    @AfterMapping
    default void setDue(@MappingTarget MemoryItemDto memoryItemDto, MemoryItem memoryItem){
            memoryItemDto.setDue(memoryItem.getNextReviewDate() != null && memoryItem.getNextReviewDate().isBefore(LocalDateTime.now()));
    }
    MemoryItem toEntity(MemoryItemRequest request);
}
