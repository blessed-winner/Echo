package org.xenon.knowspace.mappers;

import org.mapstruct.Mapper;
import org.xenon.knowspace.dtos.NoteDto;
import org.xenon.knowspace.entities.Note;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteDto toDto(Note note);
    Note toEntity(NoteDto noteDto);
}
