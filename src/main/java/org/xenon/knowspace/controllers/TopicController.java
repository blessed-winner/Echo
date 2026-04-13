package org.xenon.knowspace.controllers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.NoteDto;
import org.xenon.knowspace.dtos.TopicDto;
import org.xenon.knowspace.dtos.TopicRequest;
import org.xenon.knowspace.dtos.TopicUpdateRequest;
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
        TopicDto result = topicService.createTopic(request);
        var uri = uriBuilder.path("/topics/{id}").buildAndExpand(result).toUri();
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
}
