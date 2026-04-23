package org.xenon.echo.dtos;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.xenon.echo.enums.Role;

@Getter
@Setter
public class UserUpdateRequest {
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private Role role;
}
