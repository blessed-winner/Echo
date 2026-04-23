package org.xenon.echo.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xenon.echo.dtos.*;
import org.xenon.echo.entities.Topic;
import org.xenon.echo.entities.User;
import org.xenon.echo.exceptions.ForbiddenException;
import org.xenon.echo.exceptions.UserNotFoundException;
import org.xenon.echo.mappers.MemoryItemMapper;
import org.xenon.echo.mappers.NoteMapper;
import org.xenon.echo.mappers.TopicMapper;
import org.xenon.echo.repositories.MemoryItemRepository;
import org.xenon.echo.repositories.NoteRepository;
import org.xenon.echo.repositories.TopicRepository;
import org.xenon.echo.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicService {
  private final TopicRepository topicRepository;
  private final TopicMapper topicMapper;
  private final UserRepository userRepository;
  private final NoteRepository noteRepository;
  private final NoteMapper noteMapper;
    private final MemoryItemRepository memoryItemRepository;
    private final MemoryItemMapper memoryItemMapper;

    private UUID getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("User is not authenticated");
        }
        return (UUID) authentication.getPrincipal();
    }

    @Transactional
  public TopicDto createTopic(TopicRequest topicRequest){
      UUID userId = getCurrentUser();
      User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
      Optional<Topic> existingTopic = topicRepository.findByNameIgnoreCaseAndUserId(topicRequest.getName(), userId);
      if(existingTopic.isPresent()){
            throw new IllegalArgumentException("Topic with the same name already exists");
      }
      var topic = topicMapper.toEntity(topicRequest);
      topic.setUser(user);
      topic.setCreatedAt(LocalDateTime.now());

      return topicMapper.toDto(topicRepository.save(topic));
  }

  public Page<TopicDto> getTopics(int page, int size){
      UUID userId = getCurrentUser();
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      Page<Topic> topicsPage = topicRepository.findByUserId(userId, pageable);
      return topicsPage.map(topicMapper::toDto);
  }

  public TopicDto getTopicById(Long id){
      UUID userId = getCurrentUser();
      var topic = topicRepository.findById(id).orElseThrow(()->new RuntimeException("Topic not found"));
      if(!topic.getUser().getId().equals(userId)){
          throw new ForbiddenException("Cannot access this topic");
      }
      return topicMapper.toDto(topic);
  }

  @Transactional
  public TopicDto updateTopic(Long id, TopicUpdateRequest request){
      UUID userId = getCurrentUser();
      var topic = topicRepository.findById(id).orElseThrow(()->new RuntimeException("Topic not found"));
      if(!topic.getUser().getId().equals(userId)){
          throw new ForbiddenException("Cannot access this topic");
      }
      if(request.getName() != null){topic.setName(request.getName());}
      if(request.getDescription() != null){topic.setDescription(request.getDescription());}

      return topicMapper.toDto(topicRepository.save(topic));
  }

  @Transactional
  public void deleteTopic(Long id){
      UUID userId = getCurrentUser();
        var topic = topicRepository.findById(id).orElseThrow(()->new RuntimeException("Topic not found"));
        if(!topic.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this topic");
        }
        topicRepository.delete(topic);
  }

  public Page<NoteDto> getNotesPerTopic(Long id, int page, int size){
      UUID userId = getCurrentUser();
      var topic = topicRepository.findById(id).orElseThrow(()->new RuntimeException("Topic not found"));
      if(!topic.getUser().getId().equals(userId)){
          throw new ForbiddenException("Cannot access this topic");
      }
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      var notesPage = noteRepository.findAllByTopicId(id, pageable);
      return notesPage.map(noteMapper::toDto);
  }

  public Page<MemoryItemDto> getDueMemoryItemsPerTopic(Long id, int limit){
        UUID userId = getCurrentUser();
        var topic = topicRepository.findById(id).orElseThrow(()->new RuntimeException("Topic not found"));
        if(!topic.getUser().getId().equals(userId)){
            throw new ForbiddenException("Cannot access this topic");
        }
        Pageable pageable = PageRequest.of(0, limit, Sort.by("nextReviewDate").ascending());
        var memoryItemsPage = memoryItemRepository.findByNoteTopicIdAndNoteTopicUserIdAndNextReviewDateLessThanEqual(id, userId, LocalDateTime.now(), pageable);
        return memoryItemsPage.map(memoryItemMapper::toDto);
  }

  public Page<MemoryItemDto> getMemoryItemsPerTopic(Long id, int page, int size){
        UUID userId = getCurrentUser();
        var topic = topicRepository.findById(id).orElseThrow(()->new RuntimeException("Topic not found"));
        if(!topic.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot access this topic");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("nextReviewDate").ascending());
        var memoryItemsPage = memoryItemRepository.findByNoteTopicIdAndNoteTopicUserId(id, userId, pageable);
        return memoryItemsPage.map(memoryItemMapper::toDto);
  }

  public TopicSummaryDto getTopicSummary(Long id){
        UUID userId = getCurrentUser();
        var topic = topicRepository.findById(id).orElseThrow(()->new RuntimeException("Topic not found"));
        if(!topic.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot access this topic");
        }
        TopicSummaryDto dto = new TopicSummaryDto();
        dto.setTotalNotes(noteRepository.countByTopicIdAndTopicUserId(id, userId));
        dto.setTotalMemoryItems(memoryItemRepository.countAllItemsByTopicIdAndUserId(id, userId));
        dto.setDueItems(memoryItemRepository.countDueItemsByTopicIdAndUserId(id, userId));

        return dto;
  }

  public Page<TopicDto> searchTopics(String query, int page, int size){
      UUID userId = getCurrentUser();
      Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
      var topicsPage = topicRepository.findByNameContainingIgnoreCaseAndUserId(query, userId, pageable);
      return topicsPage.map(topicMapper::toDto);
  }
}
