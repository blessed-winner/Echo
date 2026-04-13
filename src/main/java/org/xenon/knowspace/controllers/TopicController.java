package org.xenon.knowspace.controllers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
