package org.xenon.knowspace.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.NoteDto;
import org.xenon.knowspace.dtos.NoteRequest;
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
}
