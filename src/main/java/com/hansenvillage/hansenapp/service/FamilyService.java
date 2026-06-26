package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {
   private final FamilyRepository familyRepository;
   private final UserRepository userRepository;

    public int getFamilySize(Long familyId) {
        return userRepository.countByFamilyId(familyId);
    }

    ///    List<UserResponse> getMYFamilyMembers()
    public List<UserResponse> getFamilyMembers(Long familyId) {

        if (!familyRepository.existsById(familyId)) {
            throw FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, familyId);
        }

        List<User> members = userRepository.findByFamilyId(familyId);

        return userMapper.toResponse(members);
    }

    public Family findById(long id) {

        return familyRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id));
    }

}
