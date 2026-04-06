package org.xenon.knowspace.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.knowspace.dtos.UserDto;
import org.xenon.knowspace.entities.Role;
import org.xenon.knowspace.services.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(required = false) Role role
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID myId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getAllUsers(role, myId));
    }
}
