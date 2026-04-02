package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.convert.DataSizeUnit;

import java.util.*;

@Entity
@Table(name = "notes")
@Data
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @Column(name = "creation_date")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "note_tag",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

}
