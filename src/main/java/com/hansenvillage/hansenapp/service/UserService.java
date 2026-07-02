package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.dto.UserResponse;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        User member = userMapper.toEntity(request);
        member.setFamilyId(currentFamilyId());
        return userRepository.save(member);
    }

    public User updateUser(UUID id, String newName) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));
        user.setName(newName);

        return user;
    }

    public List<UserResponse> getFamilyMembers(UUID familyId) {

        if (!familyRepository.existsById(familyId)) {
            throw FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, familyId);
        }

        List<User> members = userRepository.findByFamilyId(familyId);

        return userMapper.toResponse(members);
    }

    public void removeMember(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, id));

        userRepository.delete(user);
        }
}
