package org.xenon.echo.mappers;

import org.mapstruct.Mapper;
import org.xenon.echo.dtos.TagDto;
import org.xenon.echo.dtos.TagRequest;
import org.xenon.echo.entities.Tag;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toDto(Tag tag);
    Tag toEntity(TagRequest request);
}
