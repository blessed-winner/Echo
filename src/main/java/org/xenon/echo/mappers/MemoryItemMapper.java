package org.xenon.echo.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.xenon.echo.dtos.MemoryItemDto;
import org.xenon.echo.dtos.MemoryItemRequest;
import org.xenon.echo.entities.MemoryItem;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = {TagMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MemoryItemMapper {
    MemoryItemDto toDto(MemoryItem memoryItem);
    @AfterMapping
    default void setDue(@MappingTarget MemoryItemDto memoryItemDto, MemoryItem memoryItem){
            memoryItemDto.setDue(memoryItem.getNextReviewDate() != null && memoryItem.getNextReviewDate().isBefore(LocalDateTime.now()));
    }
    MemoryItem toEntity(MemoryItemRequest request);
    void updateFromDto(MemoryItemRequest request, @MappingTarget MemoryItem memoryItem);
}
