package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.TagDto;
import org.xenon.knowspace.dtos.TagRequest;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.mappers.TagMapper;
import org.xenon.knowspace.repositories.TagRepository;
import org.xenon.knowspace.repositories.UserRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final UserRepository userRepository;

    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public TagDto createTag(TagRequest tagRequest) {
        UUID userId = getCurrentUser();
        var user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        var existingTag = tagRepository.findByNameIgnoreCaseAndUserId(tagRequest.getName(), userId);
        if(existingTag.isPresent()){
            throw new ForbiddenException("Tag with the same name already exists");
        }
        var tag = tagMapper.toEntity(tagRequest);
        tag.setUser(user);

        return tagMapper.toDto(tagRepository.save(tag));
    }
}
