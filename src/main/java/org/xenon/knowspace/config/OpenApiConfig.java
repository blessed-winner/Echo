package org.xenon.knowspace.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth"),
        tags = {
                @Tag(name = "Auth", description = "Authentication endpoints"),
                @Tag(name = "Memory", description = "Memory management"),
                @Tag(name = "Note", description = "Note management"),
                @Tag(name = "Tags", description = "Tag operations"),
                @Tag(name = "User", description = "User operations")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}

