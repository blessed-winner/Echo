package org.xenon.echo.mappers;

import org.mapstruct.Mapper;
import org.xenon.echo.dtos.TopicDto;
import org.xenon.echo.dtos.TopicRequest;
import org.xenon.echo.entities.Topic;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    TopicDto toDto(Topic topic);
    Topic toEntity(TopicRequest request);
}
