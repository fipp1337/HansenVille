package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.FamilyRole;
import com.hansenvillage.hansenapp.entity.Role;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.FamilyRoleMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.FamilyRoleRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final FamilyRoleRepository familyRoleRepository;
    private final UserMapper userMapper;

    private final FamilyMapper familyMapper;
    private final FamilyRoleMapper familyRoleMapper;

    @Transactional
    public Family registerFamily(FamilyRegistrationRequest request) {
        if (familyRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email used");
        }

        Family family = familyMapper.toEntity(request);
        family.setPassword(passwordEncoder.encode(request.getPassword()));
        family.setMemberCount(request.getMembers().size());

        Family savedFamily = familyRepository.save(family);

        FamilyRole familyRole = familyRoleMapper.createUserRole(savedFamily.getId());
        familyRoleRepository.save(familyRole);



        List<User> userList = userMapper.toEntityList(request.getMembers());
        userList.forEach(user -> user.setFamilyId(savedFamily.getId()));
        userRepository.saveAll(userList);

        return savedFamily;
    }
}
