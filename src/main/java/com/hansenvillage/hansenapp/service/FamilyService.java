package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.UserResponse;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyService {
   private final FamilyRepository familyRepository;
   private final UserRepository userRepository;
   private final UserMapper userMapper;

    public int getFamilySize(UUID familyId) {
        return userRepository.countByFamilyId(familyId);
    }

    ///    List<UserResponse> getMYFamilyMembers() ???

    public Family findById(UUID id) {

        return familyRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id));
    }


}
