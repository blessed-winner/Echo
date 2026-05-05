package org.xenon.echo.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.xenon.echo.repositories.UserRepository;

import java.io.IOException;

@Component
@AllArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        var user = userRepository.findByEmail(oAuth2User.getAttribute("email")).orElseGet(()->authService.createUserFromOauth(oAuth2User));
        String token = jwtService.generateAccessToken(user);
        getRedirectStrategy().sendRedirect(request,response,"/auth/success?token=" + token);
    }
}
