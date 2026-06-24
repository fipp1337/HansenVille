package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;

    @Transactional
    public Family registerFamily(FamilyRegistrationRequest familyRegistrationRequest) {
        if (familyRepository.existsByEmail(familyRegistrationRequest.getEmail())) {
            throw new IllegalStateException("Email used");
        }
        if (familyRepository.existsByUsername(familyRegistrationRequest.getUsername())) {
            throw new IllegalStateException("Login used");
        }
        Family family = new Family();
        family.setUsername(familyRegistrationRequest.getUsername());
        family.setEmail(familyRegistrationRequest.getEmail());
        family.setPassword(familyRegistrationRequest.getPassword());
        family.setAddress(familyRegistrationRequest.getAddress());

        family = familyRepository.save(family);

        //// Тут надо как-то через цикл сделать метод для добавления всех переданных членов семьи, но мне в падлу в 3 ночи это делать
        return family;
    }
}
