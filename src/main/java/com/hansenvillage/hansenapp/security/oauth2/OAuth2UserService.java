package com.hansenvillage.hansenapp.security.oauth2;

import com.hansenvillage.hansenapp.entity.AdminUser;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.repository.AdminUserRepository;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final FamilyRepository familyRepository;
    private final AdminUserRepository adminUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            OAuth2Error error = new OAuth2Error("email_not_found", "Email not found from Google provider", null);
            throw new OAuth2AuthenticationException(error, error.getDescription());
        }

        String normalizedEmail = email.trim().toLowerCase();

        Optional<AdminUser> adminUser = adminUserRepository.findByEmailIgnoreCase(normalizedEmail);
        if (adminUser.isPresent()) {
            return new CustomOAuth2User(oAuth2User, adminUser.get(), null);
        }

        Family family = familyRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    OAuth2Error error = new OAuth2Error("user_not_found", "User with email " + email + " not found", null);
                    return new OAuth2AuthenticationException(error, error.getDescription());
                });

        return oAuth2User;
    }
}
