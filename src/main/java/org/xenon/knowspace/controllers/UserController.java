package org.xenon.knowspace.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.xenon.knowspace.dtos.RegisterUserRequest;
import org.xenon.knowspace.entities.Role;
import org.xenon.knowspace.entities.User;
import org.xenon.knowspace.mappers.UserMapper;
import org.xenon.knowspace.repositories.UserRepository;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ){
         if(userRepository.existsByEmail(request.getEmail())){
             return ResponseEntity.badRequest().body(Map.of("Email", "Email already exists"));
         }

         User user = userMapper.toEntity(request);
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         user.setRole(Role.USER);
         user.setCreatedAt(new Date());
         userRepository.save(user);
         var userDto = userMapper.toDto(user);
         var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

         return ResponseEntity.created(uri).body(userDto);

    }
}
