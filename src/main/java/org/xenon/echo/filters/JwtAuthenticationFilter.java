package org.xenon.echo.filters;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.xenon.echo.services.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        String token = authHeader.replace("Bearer ", "");
        try{
            UUID userId = jwtService.extractUserId(token);
            String role = jwtService.extractUserRole(token);
            var authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            //Authentication details are used to store additional information about the authentication request, such as the remote IP address and session ID. This can be useful for auditing and logging purposes, as well as for implementing additional security measures based on the context of the authentication.
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch(JwtException | IllegalArgumentException e){
            SecurityContextHolder.clearContext();
            logger.debug("JWT authentication failed: ", e);
        }

        filterChain.doFilter(request,response);

    }
}
