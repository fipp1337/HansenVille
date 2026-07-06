package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.dto.UserResponse;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private FamilyRepository familyRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void addNewMember_ShouldSaveMemberWithCurrentFamilyId() {

        AddMemberRequest request = new AddMemberRequest();
        User mappedUser = new User();
        User savedUser = new User();
        UUID mockFamilyId = UUID.randomUUID();


        mappedUser.setFamilyId(mockFamilyId);

        when(userMapper.toEntity(request)).thenReturn(mappedUser);

        when(familyRepository.incrementMemberCount(mockFamilyId)).thenReturn(1);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(mockFamilyId);


            User result = userService.addNewMember(request);


            assertNotNull(result);
            assertEquals(mockFamilyId, mappedUser.getFamilyId());
            verify(userRepository, times(1)).save(mappedUser);
        }
    }

    @Test
    void updateUser_ShouldModifyNameAndSave_WhenUserExists() {

        UUID userId = UUID.randomUUID();
        String newName = "Dmitry";
        User existingUser = new User();
        existingUser.setName("Old name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);


        User result = userService.updateUser(userId, newName);


        assertNotNull(result);
        assertEquals(newName, result.getName());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        FamilyException exception = assertThrows(FamilyException.class, () -> {
            userService.updateUser(userId, "Имя");
        });
        assertEquals(FamilyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getFamilyMembers_ShouldReturnResponses_WhenFamilyExists() {

        UUID familyId = UUID.randomUUID();
        List<User> members = List.of(new User(), new User());
        List<UserResponse> expectedResponses = List.of(new UserResponse(), new UserResponse());

        when(familyRepository.existsById(familyId)).thenReturn(true);
        when(userRepository.findByFamilyId(familyId)).thenReturn(members);
        when(userMapper.toResponse(members)).thenReturn(expectedResponses);


        List<UserResponse> actualResponses = userService.getFamilyMembers(familyId);


        assertNotNull(actualResponses);
        assertEquals(2, actualResponses.size());
        verify(userRepository).findByFamilyId(familyId);
    }

    @Test
    void getFamilyMembers_ShouldThrowException_WhenFamilyDoesNotExist() {

        UUID familyId = UUID.randomUUID();
        when(familyRepository.existsById(familyId)).thenReturn(false);


        FamilyException exception = assertThrows(FamilyException.class, () -> {
            userService.getFamilyMembers(familyId);
        });
        assertEquals(FamilyErrorCode.FAMILY_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).findByFamilyId(any());
    }

    @Test
    void removeMember_ShouldDeleteUser_WhenUserExists() {

        UUID userId = UUID.randomUUID();
        User user = new User();
        UUID mockFamilyId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(familyRepository.decrementMemberCount(mockFamilyId)).thenReturn(1);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(mockFamilyId);


            userService.removeMember(userId);


            verify(userRepository, times(1)).delete(user);
        }
    }

    @Test
    void removeMember_ShouldThrowException_WhenUserNotFound() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        FamilyException exception = assertThrows(FamilyException.class, () -> {
            userService.removeMember(userId);
        });
        assertEquals(FamilyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).delete(any());
    }
}