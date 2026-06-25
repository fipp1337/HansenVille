package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.mapper.UserMapper;
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
    private final UserMapper userMapper;

    @Transactional
    public User addNewMember(Long familyId, AddMemberRequest request) {

        familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found with id: " + familyId));

        User member = userMapper.toEntity(request);

        return userRepository.save(member);
    }
}
