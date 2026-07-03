package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.dto.UserResponse;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityFamily;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FamilyRepository familyRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @Test
    void addNewMember() {

        AddMemberRequest request = new AddMemberRequest();
        User user = new User();
        UUID familyId = UUID.randomUUID();

        SecurityFamily securityFamily = mock(SecurityFamily.class);
        when(securityFamily.getId()).thenReturn(familyId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityFamily);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userMapper.toEntity(request)).thenReturn(user);
        when(familyRepository.incrementMemberCount(any())).thenReturn(1);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.addNewMember(request);
        assertEquals(user, result);

        verify(userMapper).toEntity(request);
        verify(familyRepository).incrementMemberCount(any());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser() {

        UUID id = UUID.randomUUID();

        User user = new User();
        user.setName("Old");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User updated = userService.updateUser(id, "New");

        assertEquals(user, updated);

        verify(userRepository).findById(id);
    }

    @Test
    void updateUser_exception() {

        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        FamilyException exception = assertThrows(
                FamilyException.class,
                () -> userService.updateUser(id, "John")
        );

        assertEquals(FamilyErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verify(userRepository).findById(id);
    }

    @Test
    void getFamilyMembers() {

        UUID id = UUID.randomUUID();

        List<User> members = List.of(new User());
        List<UserResponse> responses = List.of(new UserResponse());
        
        when(familyRepository.existsById(id)).thenReturn(true);
        when(userRepository.findByFamilyId(id)).thenReturn(members);
        when(userMapper.toResponse(members)).thenReturn(responses);

        List<UserResponse> result =
                userService.getFamilyMembers(id);

        assertEquals(responses, result);

        verify(userRepository).findByFamilyId(id);
        verify(userMapper).toResponse(members);
    }

    @Test
    void removeMember() {

        UUID familyId = UUID.randomUUID();

        SecurityFamily securityFamily = mock(SecurityFamily.class);
        when(securityFamily.getId()).thenReturn(familyId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityFamily);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        UUID id = UUID.randomUUID();
        User user = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(familyRepository.decrementMemberCount(familyId)).thenReturn(1);

        userService.removeMember(id);

        verify(userRepository).findById(id);
        verify(familyRepository).decrementMemberCount(familyId);
        verify(userRepository).delete(user);
    }
}