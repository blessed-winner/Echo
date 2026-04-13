package org.xenon.knowspace.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tag")
@RestController
@RequestMapping("/tags")
@AllArgsConstructor
public class TagController {
}
