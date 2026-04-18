package org.xenon.echo.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.enums.Role;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private Role role;
}
