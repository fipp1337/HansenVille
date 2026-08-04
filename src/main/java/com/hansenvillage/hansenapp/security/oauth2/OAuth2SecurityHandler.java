package com.hansenvillage.hansenapp.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.Role;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final FamilyRepository familyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> AppException.of(AppErrorCode.FAMILY_NOT_FOUND));

        List<Role> roles = List.of(Role.USER);

        String accessToken = jwtService.generateToken(family, roles);
        String refreshToken = jwtService.generateRefreshToken(family, roles);

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
