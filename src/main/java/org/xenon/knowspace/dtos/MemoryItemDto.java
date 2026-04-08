package org.xenon.knowspace.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class MemoryItemDto {
        private Long id;

        private String text;

        private String source;

        private LocalDateTime createdAt;

        private LocalDateTime lastReviewed;

        private LocalDateTime nextReviewDate;

        private int interval;

        private float easeFactor;

        private int reviewCount;

        private boolean due;

        private Long noteId;

        private Set<TagDto> tags;
}
