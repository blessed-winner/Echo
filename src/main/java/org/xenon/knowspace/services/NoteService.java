package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.NoteDto;
import org.xenon.knowspace.dtos.NoteRequest;
import org.xenon.knowspace.entities.Note;
import org.xenon.knowspace.entities.Tag;
import org.xenon.knowspace.entities.User;
import org.xenon.knowspace.exceptions.ForbiddenException;
import org.xenon.knowspace.exceptions.UserNotFoundException;
import org.xenon.knowspace.mappers.NoteMapper;
import org.xenon.knowspace.repositories.NoteRepository;
import org.xenon.knowspace.repositories.TagRepository;
import org.xenon.knowspace.repositories.TopicRepository;
import org.xenon.knowspace.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class NoteService {
    private final UserRepository userRepository;
    private final NoteMapper noteMapper;
    private final TopicRepository topicRepository;
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication();
    }

    public NoteDto createNote(NoteRequest noteRequest){
        User currentUser = getCurrentUser();
        var topic = topicRepository.findById(noteRequest.getTopicId()).orElseThrow();
        if(!topic.getUser().getId().equals(currentUser.getId())){
            throw new ForbiddenException("Cannot add note to this topic");
        }
        Note note = noteMapper.toEntity(noteRequest);
        note.setTopic(topic);
        Set<Tag> tags = new HashSet<>();
        for(Long tagId : noteRequest.getTagIds()){
            Tag tag = tagRepository.findById(tagId).orElseThrow(()->new RuntimeException("Tag Not Found"));
            if(!tag.getUser().getId().equals(currentUser.getId())){
                throw new ForbiddenException("Cannot add this tag to note" + tag.getName());
            }
            tags.add(tag);
        }

        note.setTags(tags);
        noteRepository.save(note);

        return noteMapper.toDto(note);

    }
}
