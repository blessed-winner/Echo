package org.xenon.knowspace.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public class TopicDto {
    private Long id;

    private String name;

    private String description;

    private LocalDateTime createdAt;

    private UserDto user;

    private Set<NoteDto> notes;

}
