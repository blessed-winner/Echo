package org.xenon.echo.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xenon.echo.filters.JwtAuthenticationFilter;
import org.xenon.echo.services.JwtService;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class AppConfig {
    private final JwtService jwtService;
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){return new JwtAuthenticationFilter(jwtService);}

    @Bean
    public PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

}
