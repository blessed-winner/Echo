package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.echo.dtos.*;
import org.xenon.echo.services.MemoryItemService;
import org.xenon.echo.services.ReviewService;

import java.util.UUID;

@Tag(name = "Memory")
@RestController
@RequestMapping("/memories")
@AllArgsConstructor
public class MemoryItemController {
    private final MemoryItemService memoryItemService;
    private final ReviewService reviewService;
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

    @GetMapping("/due")
    public ResponseEntity<Page<MemoryItemDto>> dueMemoryItems(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long tagId
    ){
        return ResponseEntity.ok(memoryItemService.getDueMemoryItems(limit,tagId));
    }

    @GetMapping("/stats")
    public ResponseEntity<MemoryStatsDto> getMemoryItemStats(){
        return ResponseEntity.ok(memoryItemService.getStats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemoryItemDto> getMemoryItem(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(memoryItemService.getMemoryItem(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoryItemDto> updateMemoryItem(
            @PathVariable Long id,
            @Valid @RequestBody MemoryItemUpdateRequest request
    ){
        return ResponseEntity.ok(memoryItemService.updateMemoryItem(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemoryItem(
            @PathVariable Long id
    ){
        memoryItemService.deleteMemoryItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<MemoryItemDto> reviewMemoryItem(
            @PathVariable Long id,
            @RequestBody ReviewRequest request
    ){
        return ResponseEntity.ok(memoryItemService.review(id, request.getRating(), request.getTimeSpentSeconds()));
    }

    @PostMapping("/{id}/reschedule")
    public ResponseEntity<Void> rescheduleItem(
            @PathVariable Long id,
            @RequestBody RescheduleRequest request
    ){
        memoryItemService.reschedule(id,request.getType());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<Page<ReviewDto>> getReviewsPerItem(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(reviewService.getReviewsPerItem(id));
    }
}
