package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.xenon.echo.dtos.UserDto;
import org.xenon.echo.enums.Role;
import org.xenon.echo.services.UserService;

import java.util.List;
import java.util.UUID;

@Tag(name = "User")
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(required = false) Role role
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID myId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getAllUsers(role, myId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID id,
            @RequestBody UserDto userDto
    ){
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }
}
