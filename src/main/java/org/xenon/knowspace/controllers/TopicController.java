package org.xenon.knowspace.controllers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.*;
import org.xenon.knowspace.entities.Topic;
import org.xenon.knowspace.services.TopicService;

@RestController
@RequestMapping("/topics")
@AllArgsConstructor
public class TopicController {
    private final TopicService topicService;
    @PostMapping
    public ResponseEntity<TopicDto> createTopic(
            @Valid @RequestBody TopicRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var result = topicService.createTopic(request);
        var uri = uriBuilder.path("/topics/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @GetMapping
    public ResponseEntity<Page<TopicDto>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(topicService.getTopics(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicDto> getTopic(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(topicService.getTopicById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicDto> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicUpdateRequest request
    ){
        return ResponseEntity.ok(topicService.updateTopic(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id){
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<Page<NoteDto>> getAllNotesPerTopic(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(topicService.getNotesPerTopic(id, page, size));
    }

    @GetMapping("/{id}/due")
    public ResponseEntity<Page<MemoryItemDto>> getDueItemsPerTopic(
            @PathVariable Long id,
            @RequestParam(defaultValue = "20") int limit
    ){
        return ResponseEntity.ok(topicService.getDueMemoryItemsPerTopic(id, limit));
    }

    @GetMapping("/{id}/memories")
    public ResponseEntity<Page<MemoryItemDto>> getMemoriesPerTopic(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(topicService.getMemoryItemsPerTopic(id, page, size));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<TopicSummaryDto> getTopicSummary(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(topicService.getTopicSummary(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TopicDto>> searchTopics(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(topicService.searchTopics(query, page, size));
    }
}
