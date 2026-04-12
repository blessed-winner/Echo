package org.xenon.knowspace.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.NoteDto;
import org.xenon.knowspace.dtos.NoteRequest;
import org.xenon.knowspace.dtos.NoteUpdateRequest;
import org.xenon.knowspace.services.NoteService;

@Tag(name = "Note")
@RestController
@RequestMapping("/notes")
@AllArgsConstructor
public class NoteController {
    private final NoteService noteService;
    @PostMapping
    public ResponseEntity<NoteDto> createNote(
            @Valid @RequestBody NoteRequest request,
            UriComponentsBuilder uriBuilder
    ){
        NoteDto result = noteService.createNote(request);
        var uri = uriBuilder.path("/notes/{id}").buildAndExpand(result).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping
    public ResponseEntity<Page<NoteDto>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(noteService.getAllNotes(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNoteById(@PathVariable Long id){
        return ResponseEntity.ok(noteService.getNote(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteUpdateRequest request
    ){
        return ResponseEntity.ok(noteService.updateNote(id, request));
    }
}
