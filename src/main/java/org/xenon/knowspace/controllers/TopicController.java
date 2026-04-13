package org.xenon.knowspace.controllers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.TopicDto;
import org.xenon.knowspace.dtos.TopicRequest;
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
}
