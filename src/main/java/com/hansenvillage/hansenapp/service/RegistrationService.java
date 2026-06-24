package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.FamilyRole;
import com.hansenvillage.hansenapp.entity.Role;
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
    public Family registerFamily(FamilyRegistrationRequest request) {
        if (familyRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email used");
        }

        Family family = new Family();
        family.setEmail(request.getEmail());
        family.setPassword(request.getPassword());
        family.setAddress(request.getAddress());

        family.setMemberCount(request.getMembers().size());

        family = familyRepository.save(family);

        FamilyRole familyRole = new FamilyRole();
        familyRole.setFamilyId(family);
        familyRole.setRole(Role.USER);



        for (FamilyRegistrationRequest.MemberRequest memberReq : request.getMembers()) {
            User member = new User();
            member.setName(memberReq.getName());
            member.setFamilyId(family.getId());

            userRepository.save(member);

        }

        return family;
    }
}
