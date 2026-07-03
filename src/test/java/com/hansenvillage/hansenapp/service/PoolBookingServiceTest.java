package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PoolBookingServiceTest {

    @Mock
    private PoolSessionRepository poolSessionRepository;
    @Mock
    private PoolBookingRepository poolBookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PoolBookingMapper poolBookingMapper;

    @InjectMocks
    PoolBookingService poolBookingService;

    @Test
    void book() {

    }

    @Test
    void getBookingById_ShouldReturnBooking_WhenBookingExists() {
        UUID bookingId = UUID.randomUUID();
        PoolBooking expectedBooking = new PoolBooking();
        expectedBooking.setId(bookingId);

        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        PoolBooking result = poolBookingService.getBookingById(bookingId);

        assertEquals(bookingId, result.getId());

        verify(poolBookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getAllBookings() {
    }

    @Test
    void getBookingsByUserId_ShouldReturnList_WhenUserAndFamilyAreValid() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();

        User testUser = new User();
        testUser.setId(userId);
        testUser.setFamilyId(familyId);

        PoolBooking booking = new PoolBooking();
        List<PoolBooking> expectedBookings = List.of(booking);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(poolBookingRepository.findByUserId(userId)).thenReturn(expectedBookings);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::isAdmin).thenReturn(false);
            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);

            List<PoolBooking> result = poolBookingService.getBookingsByUserId(userId);

            assertEquals(1, result.size());
            assertEquals(expectedBookings, result);
        }
    }

    @Test
    void getBookingsByFamilyId() {
    }

    @Test
    void deleteBooking_ShouldDecreaseBookedCountAndDelete_WhenAdminDeletes() {
        UUID bookingId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        PoolBooking booking = new PoolBooking();
        booking.setId(bookingId);
        booking.setPoolSessionId(sessionId);

        PoolSession session = new PoolSession();
        session.setId(sessionId);
        session.setBookedCount(5);
        session.setSessionDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(12, 0));

        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::isAdmin).thenReturn(true);

            poolBookingService.deleteBooking(bookingId);

            assertEquals(4, session.getBookedCount());

            verify(poolSessionRepository).save(session);

            verify(poolBookingRepository).deleteById(bookingId);
        }
    }

    @Test
    void getBookingDetailsForSession_ShouldThrowException_WhenSessionDoesNotExist() {
        UUID sessionId = UUID.randomUUID();
        when(poolSessionRepository.existsById(sessionId)).thenReturn(false);
        FamilyException exception = assertThrows(FamilyException.class, () -> {
            poolBookingService.getBookingDetailsForSession(sessionId);
        });

        assertEquals(FamilyErrorCode.POOL_SESSION_NOT_FOUND, exception.getErrorCode());

        verify(poolBookingRepository, never()).findBookingDetailsBySessionId(any());
    }
}