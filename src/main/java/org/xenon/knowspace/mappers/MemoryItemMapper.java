package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.entities.MemoryItem;

@Mapper(componentModel = "spring")
public interface MemoryItemMapper {
    MemoryItemDto toDto(MemoryItem memoryItem);
    MemoryItem toEntity(MemoryItemRequest request);
}
