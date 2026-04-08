package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.xenon.knowspace.dtos.TagDTO;
import org.xenon.knowspace.entities.Tag;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDTO toDto(Tag tag);
     Tag toEntity(TagDTO tagDto);
}
