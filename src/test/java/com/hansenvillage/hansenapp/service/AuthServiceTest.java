//package com.hansenvillage.hansenapp.service;
//
//import com.hansenvillage.hansenapp.dto.*;
//import com.hansenvillage.hansenapp.entity.*;
//import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
//import com.hansenvillage.hansenapp.exception.FamilyException;
//import com.hansenvillage.hansenapp.mapper.*;
//import com.hansenvillage.hansenapp.repository.*;
//import com.hansenvillage.hansenapp.security.JwtService;
//import com.hansenvillage.hansenapp.security.SecurityFamily;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock private UserRepository userRepository;
//    @Mock private FamilyRepository familyRepository;
//    @Mock private FamilyRoleRepository familyRoleRepository;
//    @Mock private JwtService jwtService;
//    @Mock private PasswordEncoder passwordEncoder;
//    @Mock private UserMapper userMapper;
//    @Mock private FamilyMapper familyMapper;
//    @Mock private FamilyRoleMapper familyRoleMapper;
//
//    @InjectMocks
//    private AuthService authService;
//    @Test
//    void registerFamily_ShouldThrowException_WhenEmailAlreadyExists() {
//        FamilyRegistrationRequest request = new FamilyRegistrationRequest();
//        request.setEmail("test@family.com");
//
//        when(familyRepository.existsByEmail(request.getEmail())).thenReturn(true);
//
//        FamilyException exception = assertThrows(FamilyException.class, () -> {
//            authService.registerFamily(request);
//        });
//
//        assertEquals(FamilyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
//        verify(familyRepository, never()).save(any());
//    }
//
//    @Test
//    void registerFamily_ShouldRegisterSuccessfully_WhenRequestIsValid() {
//        FamilyRegistrationRequest request = new FamilyRegistrationRequest();
//        request.setEmail("new@family.com");
//        request.setPassword("raw_password");
//        request.setMembers(Collections.emptyList());
//
//        Family familyEntity = new Family();
//        Family savedFamily = new Family();
//        savedFamily.setId(UUID.randomUUID());
//        FamilyRole familyRole = new FamilyRole();
//        List<User> userList = List.of(new User(), new User());
//
//        when(familyMapper.toEntity(request)).thenReturn(familyEntity);
//        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password");
//        when(familyRepository.save(familyEntity)).thenReturn(savedFamily);
//        when(familyRoleMapper.createUserRole(savedFamily.getId())).thenReturn(familyRole);
//        when(userMapper.toEntityList(request.getMembers())).thenReturn(userList);
//
//        Family result = authService.registerFamily(request);
//
//        assertNotNull(result);
//        assertEquals(savedFamily.getId(), result.getId());
//
//        userList.forEach(user -> assertEquals(savedFamily.getId(), user.getFamilyId()));
//
//        verify(familyRepository).save(familyEntity);
//        verify(familyRoleRepository).save(familyRole);
//        verify(userRepository).saveAll(userList);
//    }
//
//
//    @Test
//    void login_ShouldThrowException_WhenEmailNotFound() {
//
//        LoginRequest request = new LoginRequest();
//        request.setEmail("wrong@mail.com");
//
//        when(familyRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
//
//        FamilyException exception = assertThrows(FamilyException.class, () -> {
//            authService.login(request);
//        });
//
//        assertEquals(FamilyErrorCode.WRONG_EMAIL, exception.getErrorCode());
//    }
//
//    @Test
//    void login_ShouldThrowException_WhenPasswordIsIncorrect() {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("exist@mail.com");
//        request.setPassword("wrong_pass");
//
//        Family family = new Family();
//        family.setPassword("correct_encoded_pass");
//
//        when(familyRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(family));
//        when(passwordEncoder.matches("wrong_pass", "correct_encoded_pass")).thenReturn(false);
//
//        FamilyException exception = assertThrows(FamilyException.class, () -> {
//            authService.login(request);
//        });
//
//        assertEquals(FamilyErrorCode.WRONG_PASSWORD, exception.getErrorCode());
//    }
//
//    @Test
//    void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("user@mail.com");
//        request.setPassword("password");
//
//        UUID familyId = UUID.randomUUID();
//        Family family = new Family();
//        family.setId(familyId);
//        family.setPassword("encoded_password");
//
//        FamilyRole dbRole = new FamilyRole();
//        dbRole.setRole("ADMIN");
//
//        when(familyRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(family));
//        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
//        when(familyRoleRepository.findByFamilyId(familyId)).thenReturn(List.of(dbRole));
//
//        when(jwtService.generateToken(eq(family), anyList())).thenReturn("access_token");
//        when(jwtService.generateRefreshToken(eq(family), anyList())).thenReturn("refresh_token");
//
//        LoginResponse response = authService.login(request);
//
//        assertNotNull(response);
//        assertEquals("access_token", response.getAccessToken());
//        assertEquals("refresh_token", response.getRefreshToken());
//    }
//
//
//    @Test
//    void refreshToken_ShouldReturnNewTokens_WhenTokenIsValid() {
//        RefreshRequest request = new RefreshRequest();
//        request.setRefreshToken("old_refresh_token");
//
//        Family baseFamily = new Family();
//        baseFamily.setId(UUID.randomUUID());
//        baseFamily.setEmail("family@mail.com");
//        baseFamily.setPassword("");
//
//        List<Role> roles = List.of(Role.ADMIN);
//
//        SecurityFamily securityFamily = new SecurityFamily(baseFamily, roles);
//
//        when(jwtService.parseRefreshToken("old_refresh_token")).thenReturn(securityFamily);
//        when(jwtService.generateToken(any(Family.class), eq(roles))).thenReturn("new_access");
//        when(jwtService.generateRefreshToken(any(Family.class), eq(roles))).thenReturn("new_refresh");
//
//        LoginResponse response = authService.refreshToken(request);
//
//        assertNotNull(response);
//        assertEquals("new_access", response.getAccessToken());
//        assertEquals("new_refresh", response.getRefreshToken());
//    }
//}