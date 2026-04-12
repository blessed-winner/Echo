package org.xenon.knowspace.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class NoteDto {
    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private TopicDto topic;

    private Set<TagDto> tags;

    private Set<MemoryItemDto> memoryItems;
}
