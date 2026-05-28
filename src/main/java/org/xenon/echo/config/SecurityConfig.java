package org.xenon.echo.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.xenon.echo.enums.Role;
import org.xenon.echo.services.Oauth2SuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final @Lazy Oauth2SuccessHandler oauth2SuccessHandler;
    private final AppConfig appConfig;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(appConfig.passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                         c-> c.requestMatchers("/","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                                                           .requestMatchers(HttpMethod.POST,"/auth/register").permitAll()
                                                           .requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                                                           .requestMatchers(HttpMethod.GET,"/auth/login/**").permitAll()
                                                           .requestMatchers(HttpMethod.GET,"/auth/verify").permitAll()
                                                           .requestMatchers(HttpMethod.POST,"/auth/refresh").permitAll()
                                                           .requestMatchers(HttpMethod.POST,"/auth/logout").permitAll()
                                                           .requestMatchers(HttpMethod.GET,"/auth/success").permitAll()
                                                           .requestMatchers(HttpMethod.GET,"/auth/forgot-password").permitAll()
                                                           .requestMatchers(HttpMethod.POST,"/auth/reset").permitAll()
                                                           .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                                                           .anyRequest().authenticated()
                ).addFilterBefore(appConfig.jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c->{
                    c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    c.accessDeniedHandler((request, response, accessDeniedException) -> {response.setStatus(HttpStatus.FORBIDDEN.value());});
                        }
                )
                .oauth2Login(oauth2->{
                    oauth2
                            .successHandler(oauth2SuccessHandler)
                            .failureHandler((request,response,exception)->{
                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                response.getWriter().write("Oauth2 login failed: " + exception.getMessage());
                            });
                });

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Vite default port
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
