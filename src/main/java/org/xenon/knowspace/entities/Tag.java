package org.xenon.knowspace.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "tags")
    private Set<Note> notes = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "tags")
    private Set<MemoryItem> memoryItems = new HashSet<>();
}
