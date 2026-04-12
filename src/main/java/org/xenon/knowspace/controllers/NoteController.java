package org.xenon.knowspace.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Note")
@RestController
@RequestMapping("/notes")
public class NoteController {
}
