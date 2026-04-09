package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "memory_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String text;

    private String source;

    private LocalDateTime lastReviewed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime nextReviewDate;

    private int interval;

    private double easeFactor;

    private int reviewCount;

    @ManyToMany
    @JoinTable(
            name = "memory_item_links",
            joinColumns = @JoinColumn(name = "memory_item_id"),
            inverseJoinColumns = @JoinColumn(name = "related_item_id")
    )
    private Set<MemoryItem> relatedItems = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "memory_item_tags",
            joinColumns = @JoinColumn(name = "memory_item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "memoryItem", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();
}
