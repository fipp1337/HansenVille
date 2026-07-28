package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.dto.UserResponse;
import com.hansenvillage.hansenapp.dto.UserUpdateRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final UserMapper userMapper;

    @Transactional
    public User addNewMember(AddMemberRequest request) {
        UUID familyId = SecurityUtils.currentFamilyId();

        User member = userMapper.toEntity(request);
        member.setFamilyId(familyId);
        User savedUser = userRepository.save(member);

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> AppException.of(AppErrorCode.FAMILY_NOT_FOUND, familyId));
        family.setMemberCount(family.getMemberCount() + 1);
        familyRepository.save(family);

        log.info("Member added: user={}, family={}", savedUser.getId(), familyId);
        return savedUser;
    }

    @Transactional
    public User updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, id));
        SecurityUtils.assertOwnerOrSuperAdmin(user.getFamilyId());

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getFamilyMembers(UUID id) {
        if (!familyRepository.existsById(id)) {
            throw AppException.of(AppErrorCode.FAMILY_NOT_FOUND, id);
        }
        return userMapper.toResponse(userRepository.findByFamilyId(id));
    }

    @Transactional
    public void removeMember(UUID memberId) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, memberId));

        SecurityUtils.assertOwnerOrSuperAdmin(user.getFamilyId());
        userRepository.delete(user);

        Family family = familyRepository.findById(user.getFamilyId())
                .orElseThrow(() -> AppException.of(AppErrorCode.FAMILY_NOT_FOUND, user.getFamilyId()));
        family.setMemberCount(Math.max(0, family.getMemberCount() - 1));
        familyRepository.save(family);
        log.info("Member removed: user={}, family={}", memberId, user.getFamilyId());
    }
}
