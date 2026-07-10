package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.dto.FamilyUpdateRequest;
import com.hansenvillage.hansenapp.dto.UserResponse;
import com.hansenvillage.hansenapp.dto.UserUpdateRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.hansenvillage.hansenapp.security.SecurityUtils.currentFamilyId;

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
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND));
        family.setMemberCount(family.getMemberCount() + 1);
        familyRepository.save(family);

        return savedUser;
    }

    @Transactional
    public User updateUser(UUID id, UserUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, id));
        SecurityUtils.assertOwner(user.getFamilyId());

        if (request.getName() != null && !request.getName().isBlank()) user.setName(request.getName());
        if (request.getAge() != null) user.setAge(request.getAge());

        return userRepository.save(user);
    }

    public List<UserResponse> getFamilyMembers(UUID id) {

        if (!familyRepository.existsById(id)) {
            throw FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, id);
        }

        List<User> members = userRepository.findByFamilyId(id);

        return userMapper.toResponse(members);
    }

    @Transactional
    public void removeMember(UUID memberId) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, memberId));

        SecurityUtils.assertOwner(user.getFamilyId());

        userRepository.delete(user);

        Family family = familyRepository.findById(user.getFamilyId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND));
        family.setMemberCount(Math.max(0, family.getMemberCount() - 1));
        familyRepository.save(family);
    }
}
