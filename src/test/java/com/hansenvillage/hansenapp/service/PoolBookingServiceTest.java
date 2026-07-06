package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
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
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    void book_ShouldThrowException_WhenOutOfTickets() {
        // --- ARRANGE ---
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        PoolBookingRequest request = new PoolBookingRequest();
        request.setUserId(userId);
        request.setPoolSessionId(sessionId);

        User user = new User();
        user.setId(userId);
        user.setFamilyId(familyId);

        PoolSession session = new PoolSession();
        session.setId(sessionId);

        session.setSessionDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.of(12, 0));
        session.setBookedCount(0);
        session.setMaxCapacity(10);

        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(poolBookingRepository.existsByUserIdAndPoolSessionId(userId, sessionId)).thenReturn(false);

        when(userRepository.countByFamilyId(familyId)).thenReturn(3);
        when(poolBookingRepository.countBookingsForFamilyInWeek(eq(familyId), any(), any())).thenReturn(6L);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);

            FamilyException exception = assertThrows(FamilyException.class, () -> {
                poolBookingService.book(request);
            });

            assertEquals(FamilyErrorCode.OUT_OF_TICKETS, exception.getErrorCode());

            verify(poolSessionRepository, never()).save(any());
            verify(poolBookingRepository, never()).save(any());
        }
    }

    @Test
    void book_ShouldSuccessfullyBookPool_WhenAllConditionsAreValid() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        PoolBookingRequest request = new PoolBookingRequest();
        request.setUserId(userId);
        request.setPoolSessionId(sessionId);

        User user = new User();
        user.setId(userId);
        user.setFamilyId(familyId);

        PoolSession session = new PoolSession();
        session.setId(sessionId);
        session.setSessionDate(LocalDate.now().plusDays(2));
        session.setStartTime(LocalTime.of(11, 0));
        session.setBookedCount(3);
        session.setMaxCapacity(10);

        PoolBooking initialBooking = new PoolBooking();
        PoolBooking savedBooking = new PoolBooking();
        savedBooking.setId(UUID.randomUUID());

        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(poolBookingRepository.existsByUserIdAndPoolSessionId(userId, sessionId)).thenReturn(false);

        when(userRepository.countByFamilyId(familyId)).thenReturn(3);
        when(poolBookingRepository.countBookingsForFamilyInWeek(eq(familyId), any(), any())).thenReturn(2L);

        when(poolBookingMapper.toEntity(request)).thenReturn(initialBooking);
        when(poolBookingRepository.save(initialBooking)).thenReturn(savedBooking);

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);

            PoolBooking result = poolBookingService.book(request);

            assertNotNull(result);
            assertEquals(savedBooking.getId(), result.getId());

            assertEquals(4, session.getBookedCount());

            verify(poolSessionRepository, times(1)).save(session);
            verify(poolBookingRepository, times(1)).save(initialBooking);
        }
    }

    @Test
    void getBookingById_ShouldReturnBooking_WhenBookingExists() {
        UUID bookingId = UUID.randomUUID();
        PoolBooking expectedBooking = new PoolBooking();
        expectedBooking.setId(bookingId);

        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        Optional<PoolBooking> result = poolBookingService.getBookById(bookingId);

        assertTrue(result.isPresent());
        assertEquals(bookingId, result.get().getId());

        verify(poolBookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getAllBookings_ShouldReturnListOfBookings() {
        List<PoolBooking> mockBookings = List.of(new PoolBooking(), new PoolBooking());
        when(poolBookingRepository.findAll()).thenReturn(mockBookings);

        List<PoolBooking> result = poolBookingService.getAllBookings();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(poolBookingRepository, times(1)).findAll();
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
    void getBookingsByFamilyId_ShouldReturnFamilyBookings() {
        UUID familyId = UUID.randomUUID();
        List<PoolBooking> mockBookings = List.of(new PoolBooking());
        when(poolBookingRepository.findByFamilyId(familyId)).thenReturn(mockBookings);

        List<PoolBooking> result = poolBookingService.getBookingsByFamilyId(familyId);

        assertEquals(1, result.size());
        verify(poolBookingRepository, times(1)).findByFamilyId(familyId);
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

    @Test
    void book_ShouldThrowOptimisticLockingException_WhenDatabaseConflictOccurs() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        PoolBookingRequest request = new PoolBookingRequest();
        request.setUserId(userId);
        request.setPoolSessionId(sessionId);

        User user = new User();
        user.setId(userId);
        user.setFamilyId(familyId);

        PoolSession session = new PoolSession();
        session.setId(sessionId);
        session.setSessionDate(LocalDate.now().plusDays(2));
        session.setStartTime(LocalTime.of(14, 0));
        session.setBookedCount(1);
        session.setMaxCapacity(10);

        PoolBooking initialBooking = new PoolBooking();

        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(poolBookingRepository.existsByUserIdAndPoolSessionId(userId, sessionId)).thenReturn(false);
        when(userRepository.countByFamilyId(familyId)).thenReturn(2);
        when(poolBookingRepository.countBookingsForFamilyInWeek(eq(familyId), any(), any())).thenReturn(0L);
        when(poolBookingMapper.toEntity(request)).thenReturn(initialBooking);

        when(poolBookingRepository.save(initialBooking))
                .thenThrow(new ObjectOptimisticLockingFailureException(PoolBooking.class, "id"));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);

            assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
                poolBookingService.book(request);
            });

            verify(poolSessionRepository, times(1)).save(session);

            verify(poolBookingRepository, times(1)).save(initialBooking);
        }
    }

    @Test
    void deleteBooking_ShouldThrowTimeOutException_WhenLessThan6HoursBeforeSession() {
        UUID bookingId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        PoolBooking booking = new PoolBooking();
        booking.setPoolSessionId(sessionId);

        LocalDateTime nearFuture = LocalDateTime.now().plusHours(2);

        PoolSession session = new PoolSession();
        session.setId(sessionId);
        session.setSessionDate(nearFuture.toLocalDate());
        session.setStartTime(nearFuture.toLocalTime());
        session.setBookedCount(5);

        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::isAdmin).thenReturn(true);

            FamilyException exception = assertThrows(FamilyException.class, () -> {
                poolBookingService.deleteBooking(bookingId);
            });

            assertEquals(FamilyErrorCode.TIME_OUT, exception.getErrorCode());

            assertEquals(5, session.getBookedCount());
            verify(poolSessionRepository, never()).save(any());
            verify(poolBookingRepository, never()).deleteById(any());
        }
    }
}