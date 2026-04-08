package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.xenon.knowspace.dtos.TagDto;
import org.xenon.knowspace.entities.Tag;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);
     Tag toEntity(TagDto tagDto);
}
