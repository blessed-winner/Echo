package org.xenon.echo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        security = @SecurityRequirement(name = "bearerAuth"),
        info = @Info(
                title = "Echo API-Spaced repetition engine",
                version = "1.0",
                description = """
                              ### Oauth
                              [Login with Google](http://localhost:8080/oauth2/authorization/google)
                              [Login with GitHub](http://localhost:8080/oauth2/authorization/github)
                              """
        ),
        tags = {
                @Tag(name = "Auth", description = "Authentication endpoints"),
                @Tag(name = "Memory", description = "Memory management"),
                @Tag(name = "Note", description = "Note management"),
                @Tag(name = "Tags", description = "Tag operations"),
                @Tag(name = "Admin",description = "User operations"),
                @Tag(name = "Topic", description = "Topic management"),
                @Tag(name = "Analytics", description = "User analytics")
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

