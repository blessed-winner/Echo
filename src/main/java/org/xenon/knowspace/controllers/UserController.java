package org.xenon.knowspace.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xenon.knowspace.dtos.UserDto;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    public ResponseEntity<List<UserDto>> getAllUsers() {}
}
