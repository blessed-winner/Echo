package org.xenon.knowspace.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.TagDto;
import org.xenon.knowspace.dtos.TagRequest;
import org.xenon.knowspace.dtos.TagResponseDto;
import org.xenon.knowspace.dtos.TagSummaryDto;
import org.xenon.knowspace.services.TagService;

import java.util.Set;

@Tag(name = "Tag")
@RestController
@RequestMapping("/tags")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;
    @PostMapping
    public ResponseEntity<TagDto> createTag(
            @Valid @RequestBody TagRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var result = tagService.createTag(request);
        var uri = uriBuilder.path("/tags/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping
    public ResponseEntity<Set<TagResponseDto>> getAllTags(){
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagRequest request
    ){
        return ResponseEntity.ok(tagService.updateTag(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id){
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<TagSummaryDto> getTagSummary(@PathVariable Long id){
        return ResponseEntity.ok(tagService.getTagSummary(id));
    }
}
