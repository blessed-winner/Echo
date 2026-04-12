package org.xenon.knowspace.services;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xenon.knowspace.dtos.NoteDto;
import org.xenon.knowspace.dtos.NoteRequest;
import org.xenon.knowspace.entities.User;

import java.util.UUID;

@Service
@AllArgsConstructor
public class NoteService {
    private UUID getCurrentUser(){
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    public NoteDto createNote(NoteRequest noteRequest){
        UUID userId = getCurrentUser();
    }
}
