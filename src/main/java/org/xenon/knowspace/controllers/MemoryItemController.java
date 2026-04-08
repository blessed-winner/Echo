package org.xenon.knowspace.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.services.MemoryItemService;

@RestController
@RequestMapping("/memories")
@AllArgsConstructor
public class MemoryItemController {
    private final MemoryItemService memoryItemService;
    @PostMapping
    public ResponseEntity<MemoryItemDto> createMemoryItem(
            @Valid @RequestBody MemoryItemRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var result = memoryItemService.createMemoryItem(request);
        var uri = uriBuilder.path("/memories/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }
}
