package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.TopicDto;
import org.xenon.knowspace.dtos.TopicRequest;
import org.xenon.knowspace.entities.Topic;
import org.xenon.knowspace.entities.User;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.exceptions.UserNotFoundException;
import org.xenon.knowspace.mappers.TopicMapper;
import org.xenon.knowspace.repositories.TopicRepository;
import org.xenon.knowspace.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TopicService {
  private final TopicRepository topicRepository;
  private final TopicMapper topicMapper;
  private final UserRepository userRepository;

  public UUID getCurrentUser(){
      return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

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
}
