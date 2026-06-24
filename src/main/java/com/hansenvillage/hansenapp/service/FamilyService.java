package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FamilyService {
   private final FamilyRepository familyRepository;
   private final UserRepository userRepository;

    public int getFamilySize(Long familyId) {
        return userRepository.countByFamilyId(familyId);
    }
}
