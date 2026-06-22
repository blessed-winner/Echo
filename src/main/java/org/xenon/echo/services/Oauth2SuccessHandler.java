package org.xenon.echo.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.xenon.echo.config.JwtConfig;
import org.xenon.echo.repositories.UserRepository;

import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final JwtConfig jwtConfig;
    private String frontendUrl;
    private boolean cookieSecure;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        var user = userRepository.findByEmail(oAuth2User.getAttribute("email")).orElseGet(()->userService.createUserFromOauth(oAuth2User));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        addRefreshTokenCookie(response, refreshToken, request.isSecure() || cookieSecure);
        String encodedToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String baseUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
        getRedirectStrategy().sendRedirect(request,response, baseUrl + "/auth/success?token=" + encodedToken);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken, boolean secure){
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        response.addCookie(cookie);
    }
}
