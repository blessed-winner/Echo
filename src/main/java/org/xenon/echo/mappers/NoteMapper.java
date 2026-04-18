package org.xenon.echo.mappers;

import org.mapstruct.Mapper;
import org.xenon.echo.dtos.NoteDto;
import org.xenon.echo.dtos.NoteRequest;
import org.xenon.echo.entities.Note;

@Mapper(componentModel = "spring", uses = {TagMapper.class, TopicMapper.class, MemoryItemMapper.class})
public interface NoteMapper {
    NoteDto toDto(Note note);
    Note toEntity(NoteRequest noteDto);
}
