package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.xenon.echo.dtos.UserDto;
import org.xenon.echo.dtos.UserUpdateRequest;
import org.xenon.echo.enums.Role;
import org.xenon.echo.services.UserService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin")
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
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
            @Valid @PathVariable UUID id,
            @RequestBody UserUpdateRequest request
            ){
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable UUID id){
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable UUID id){
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Void> toggleAdmin(@PathVariable UUID id){
        userService.changeUserRole(id);
        return ResponseEntity.noContent().build();
    }
}
