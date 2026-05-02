package org.xenon.echo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.xenon.echo.dtos.AdminSystemAnalyticsDto;
import org.xenon.echo.dtos.UserDto;
import org.xenon.echo.dtos.UserUpdateRequest;
import org.xenon.echo.enums.Role;
import org.xenon.echo.services.AnalyticsService;
import org.xenon.echo.services.UserService;

import java.util.List;
import java.util.UUID;

@Tag(name = "Admin")
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AnalyticsService analyticsService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(required = false) Role role
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID myId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getAllUsers(role, myId));
    }

    @GetMapping("/system/analytics")
    public ResponseEntity<AdminSystemAnalyticsDto> getAdminSystemAnalytics(){
        return ResponseEntity.ok(analyticsService.getSystemAnalytics());
    }

    @GetMapping("users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @DeleteMapping("users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("users/{id}")
    public ResponseEntity<UserDto> updateUser(
            @Valid @PathVariable UUID id,
            @RequestBody UserUpdateRequest request
            ){
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PutMapping("users/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable UUID id){
        userService.enableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("users/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable UUID id){
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("users/{id}/role")
    public ResponseEntity<Void> toggleAdmin(@PathVariable UUID id){
        userService.changeUserRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("users/{id}/reset-password")
    public ResponseEntity<Void> resetUserPassword(
            @PathVariable UUID id,
            @RequestBody String newPassword
    ){
        userService.resetUserPassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("users/{id}/force-verify")
    public ResponseEntity<Void> forceVerifyUser(
            @PathVariable UUID id
    ){
        userService.forceVerify(id);
        return ResponseEntity.noContent().build();
    }
}
