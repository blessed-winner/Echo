package org.xenon.knowspace.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID id;
    private String name;
    private String email;
}
