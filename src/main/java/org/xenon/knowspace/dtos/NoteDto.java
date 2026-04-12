package org.xenon.knowspace.dtos;

import java.time.LocalDateTime;
import java.util.Set;

public class NoteDto {
    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private TopicDto topic;

    private Set<TagDto> tags;

    private Set<MemoryItemDto> memoryItems;
}
