package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.xenon.knowspace.dtos.TopicDto;
import org.xenon.knowspace.entities.Topic;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    TopicDto toDto(Topic topic);
    Topic toEntity(TopicDto topicDto);
}
