package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;

    @Transactional
    public User addNewMember(Long familyId, AddMemberRequest request) {

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found with id: " + familyId));

        User member = new User();
        member.setName(request.getName());
        member.setFamilyId(family.getId());

        return userRepository.save(member);
    }
}
