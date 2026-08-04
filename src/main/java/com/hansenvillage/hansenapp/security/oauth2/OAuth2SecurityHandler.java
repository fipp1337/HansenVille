package com.hansenvillage.hansenapp.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansenvillage.hansenapp.entity.Role;
import com.hansenvillage.hansenapp.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SecurityHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken;
        String refreshToken;

        if (customUser.isAdmin()) {
            accessToken = jwtService.generateAdminToken(customUser.getAdminUser());
            refreshToken = jwtService.generateAdminRefreshToken(customUser.getAdminUser());
        } else {
            accessToken = jwtService.generateToken(customUser.getFamily(), List.of(Role.USER));
            refreshToken = jwtService.generateRefreshToken(customUser.getFamily(), List.of(Role.USER));
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());


//        // Вернуть когда будет фронт енд, а мапу убрать
//        clearAuthenticationAttributes(request);
//
//        // Перенаправляем пользователя на фронтенд с токенами в Query Parameters
//        // (Параметризуйте этот URL через application.yml для dev/prod)
//
//        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth2/redirect")
//                .queryParam("accessToken", accessToken)
//                .queryParam("refreshToken", refreshToken)
//                .build().toUriString();
//
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        Map<String, String> tokens = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

        objectMapper.writeValue(response.getWriter(), tokens);
    }
}
