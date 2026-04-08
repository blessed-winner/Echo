package org.xenon.knowspace.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.MemoryItemDto;
import org.xenon.knowspace.dtos.MemoryItemRequest;
import org.xenon.knowspace.services.MemoryItemService;

import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<Page<MemoryItemDto>> getAllMemoryItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var result = memoryItemService.getAllMemoryItems(userId,page,size);
        return ResponseEntity.ok(result);
    }
}
