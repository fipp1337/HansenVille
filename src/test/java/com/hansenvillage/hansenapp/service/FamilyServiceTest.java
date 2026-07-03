package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamilyServiceTest {

    @Mock
    private FamilyRepository familyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private FamilyService familyService;


    @Test
    void getFamilySize_ShouldReturnCorrectCount() {
        UUID familyId = UUID.randomUUID();
        int expectedSize = 4;

        when(userRepository.countByFamilyId(familyId)).thenReturn(expectedSize);

        int actualSize = familyService.getFamilySize(familyId);

        assertEquals(expectedSize, actualSize);
        verify(userRepository, times(1)).countByFamilyId(familyId);
    }


    @Test
    void findById_ShouldReturnFamily_WhenFamilyExists() {
        UUID familyId = UUID.randomUUID();
        Family expectedFamily = new Family();
        expectedFamily.setId(familyId);

        when(familyRepository.findById(familyId)).thenReturn(Optional.of(expectedFamily));

        Family actualFamily = familyService.findById(familyId);

        assertNotNull(actualFamily);
        assertEquals(familyId, actualFamily.getId());
        verify(familyRepository, times(1)).findById(familyId);
    }


    @Test
    void findById_ShouldThrowFamilyException_WhenFamilyDoesNotExist() {
        UUID familyId = UUID.randomUUID();

        when(familyRepository.findById(familyId)).thenReturn(Optional.empty());

        FamilyException exception = assertThrows(FamilyException.class, () -> {
            familyService.findById(familyId);
        });

        assertEquals(FamilyErrorCode.FAMILY_NOT_FOUND, exception.getErrorCode());

        verify(familyRepository, times(1)).findById(familyId);
    }
}