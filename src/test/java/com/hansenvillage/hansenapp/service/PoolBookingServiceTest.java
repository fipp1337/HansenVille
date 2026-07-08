//package com.hansenvillage.hansenapp.service;
//
//import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
//import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
//import com.hansenvillage.hansenapp.entity.PoolBooking;
//import com.hansenvillage.hansenapp.entity.PoolSession;
//import com.hansenvillage.hansenapp.entity.User;
//import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
//import com.hansenvillage.hansenapp.exception.FamilyException;
//import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
//import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
//import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
//import com.hansenvillage.hansenapp.repository.UserRepository;
//import com.hansenvillage.hansenapp.security.SecurityUtils;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.orm.ObjectOptimisticLockingFailureException;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PoolBookingServiceTest {
//
//    @Mock
//    private PoolSessionRepository poolSessionRepository;
//    @Mock
//    private PoolBookingRepository poolBookingRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private PoolBookingMapper poolBookingMapper;
//
//    @InjectMocks
//    private PoolBookingService poolBookingService;
//
//    @Test
//    void book_ShouldThrowException_WhenOutOfTickets() {
//        UUID userId = UUID.randomUUID();
//        UUID familyId = UUID.randomUUID();
//        UUID sessionId = UUID.randomUUID();
//
//        PoolBookingRequest request = new PoolBookingRequest();
//        request.setUserId(userId);
//        request.setPoolSessionId(sessionId);
//
//        User user = new User();
//        user.setId(userId);
//        user.setFamilyId(familyId);
//
//        PoolSession session = new PoolSession();
//        session.setId(sessionId);
//        session.setSessionDate(LocalDate.now().plusDays(1));
//        session.setStartTime(LocalTime.of(12, 0));
//        session.setBookedCount(0);
//        session.setMaxCapacity(10);
//
//        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(poolBookingRepository.existsByUserIdAndPoolSessionId(userId, sessionId)).thenReturn(false);
//        when(userRepository.countByFamilyId(familyId)).thenReturn(3);
//        when(poolBookingRepository.countBookingsForFamilyInWeek(eq(familyId), any(), any())).thenReturn(6L);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);
//
//            FamilyException exception = assertThrows(FamilyException.class, () -> {
//                poolBookingService.book(request);
//            });
//
//            assertEquals(FamilyErrorCode.OUT_OF_TICKETS, exception.getErrorCode());
//            verify(poolSessionRepository, never()).save(any());
//            verify(poolBookingRepository, never()).save(any());
//        }
//    }
//
//    @Test
//    void book_ShouldSuccessfullyBookPool_WhenAllConditionsAreValid() {
//        UUID userId = UUID.randomUUID();
//        UUID familyId = UUID.randomUUID();
//        UUID sessionId = UUID.randomUUID();
//        UUID bookingId = UUID.randomUUID();
//
//        PoolBookingRequest request = new PoolBookingRequest();
//        request.setUserId(userId);
//        request.setPoolSessionId(sessionId);
//
//        User user = new User();
//        user.setId(userId);
//        user.setFamilyId(familyId);
//
//        PoolSession session = new PoolSession();
//        session.setId(sessionId);
//        session.setSessionDate(LocalDate.now().plusDays(2));
//        session.setStartTime(LocalTime.of(11, 0));
//        session.setBookedCount(3);
//        session.setMaxCapacity(10);
//
//        PoolBooking initialBooking = new PoolBooking();
//        PoolBooking savedBooking = new PoolBooking();
//        savedBooking.setId(bookingId);
//        savedBooking.setUserId(userId);
//
//        PoolBookingResponse expectedResponse = new PoolBookingResponse();
//        expectedResponse.setBookingId(bookingId);
//
//        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(poolBookingRepository.existsByUserIdAndPoolSessionId(userId, sessionId)).thenReturn(false);
//        when(userRepository.countByFamilyId(familyId)).thenReturn(3);
//        when(poolBookingRepository.countBookingsForFamilyInWeek(eq(familyId), any(), any())).thenReturn(2L);
//        when(poolBookingMapper.toEntity(request)).thenReturn(initialBooking);
//        when(poolBookingRepository.save(initialBooking)).thenReturn(savedBooking);
//
//        // Мокаем новый интерфейсный маппер с двумя аргументами
//        when(poolBookingMapper.toResponse(savedBooking, user)).thenReturn(expectedResponse);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);
//
//            PoolBookingResponse result = poolBookingService.book(request); // Тип изменен на PoolBookingResponse
//
//            assertNotNull(result);
//            assertEquals(bookingId, result.getBookingId());
//            assertEquals(4, session.getBookedCount());
//
//            verify(poolSessionRepository, times(1)).save(session);
//            verify(poolBookingRepository, times(1)).save(initialBooking);
//        }
//    }
//
//    @Test
//    void getBookingById_ShouldReturnBooking_WhenBookingExists() {
//        UUID bookingId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//
//        PoolBooking expectedBooking = new PoolBooking();
//        expectedBooking.setId(bookingId);
//        expectedBooking.setUserId(userId);
//
//        User mockUser = new User();
//        mockUser.setId(userId);
//
//        PoolBookingResponse expectedResponse = new PoolBookingResponse();
//        expectedResponse.setBookingId(bookingId);
//
//        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
//        when(poolBookingMapper.toResponse(expectedBooking, mockUser)).thenReturn(expectedResponse);
//
//        PoolBookingResponse result = poolBookingService.getBookingById(bookingId); // Тип изменен на PoolBookingResponse
//
//        assertNotNull(result);
//        assertEquals(bookingId, result.getBookingId());
//        verify(poolBookingRepository, times(1)).findById(bookingId);
//    }
//
//    @Test
//    void getAllBookings_ShouldReturnListOfBookings() {
//        List<PoolBookingResponse> mockResponses = List.of(new PoolBookingResponse(), new PoolBookingResponse());
//
//        // Тест перенаправлен на новый производительный метод репозитория
//        when(poolBookingRepository.findAllResponses()).thenReturn(mockResponses);
//
//        List<PoolBookingResponse> result = poolBookingService.getAllBookings();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        verify(poolBookingRepository, times(1)).findAllResponses();
//    }
//
//    @Test
//    void getBookingsByUserId_ShouldReturnList_WhenUserAndFamilyAreValid() {
//        UUID userId = UUID.randomUUID();
//        UUID familyId = UUID.randomUUID();
//
//        User testUser = new User();
//        testUser.setId(userId);
//        testUser.setFamilyId(familyId);
//
//        PoolBooking booking = new PoolBooking();
//        List<PoolBooking> expectedBookings = List.of(booking);
//
//        PoolBookingResponse response = new PoolBookingResponse();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
//        when(poolBookingRepository.findByUserId(userId)).thenReturn(expectedBookings);
//        when(poolBookingMapper.toResponse(booking, testUser)).thenReturn(response);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(SecurityUtils::isAdmin).thenReturn(false);
//            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);
//
//            List<PoolBookingResponse> result = poolBookingService.getBookingsByUserId(userId); // Тип изменен
//
//            assertEquals(1, result.size());
//            assertEquals(response, result.get(0));
//        }
//    }
//
//    @Test
//    void getBookingsByFamilyId_ShouldReturnFamilyBookings() {
//        UUID familyId = UUID.randomUUID();
//        List<PoolBookingResponse> mockResponses = List.of(new PoolBookingResponse());
//
//        // Тест перенаправлен на новый метод с JPQL конструктором
//        when(poolBookingRepository.findResponsesByFamilyId(familyId)).thenReturn(mockResponses);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(SecurityUtils::isAdmin).thenReturn(false);
//            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);
//
//            List<PoolBookingResponse> result = poolBookingService.getBookingsByFamilyId(familyId); // Тип изменен
//
//            assertEquals(1, result.size());
//            verify(poolBookingRepository, times(1)).findResponsesByFamilyId(familyId);
//        }
//    }
//
//    @Test
//    void deleteBooking_ShouldDecreaseBookedCountAndDelete_WhenAdminDeletes() {
//        UUID bookingId = UUID.randomUUID();
//        UUID sessionId = UUID.randomUUID();
//
//        PoolBooking booking = new PoolBooking();
//        booking.setId(bookingId);
//        booking.setPoolSessionId(sessionId);
//
//        PoolSession session = new PoolSession();
//        session.setId(sessionId);
//        session.setBookedCount(5);
//        session.setSessionDate(LocalDate.now().plusDays(1));
//        session.setStartTime(LocalTime.of(12, 0));
//
//        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
//        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(SecurityUtils::isAdmin).thenReturn(true);
//
//            poolBookingService.deleteBooking(bookingId);
//
//            assertEquals(4, session.getBookedCount());
//            verify(poolSessionRepository).save(session);
//            verify(poolBookingRepository).deleteById(bookingId);
//        }
//    }
//
//    @Test
//    void getBookingDetailsForSession_ShouldThrowException_WhenSessionDoesNotExist() {
//        UUID sessionId = UUID.randomUUID();
//        when(poolSessionRepository.existsById(sessionId)).thenReturn(false);
//
//        FamilyException exception = assertThrows(FamilyException.class, () -> {
//            poolBookingService.getBookingDetailsForSession(sessionId);
//        });
//
//        assertEquals(FamilyErrorCode.POOL_SESSION_NOT_FOUND, exception.getErrorCode());
//        verify(poolBookingRepository, never()).findBookingDetailsBySessionId(any());
//    }
//
//    @Test
//    void book_ShouldThrowOptimisticLockingException_WhenDatabaseConflictOccurs() {
//        UUID userId = UUID.randomUUID();
//        UUID familyId = UUID.randomUUID();
//        UUID sessionId = UUID.randomUUID();
//
//        PoolBookingRequest request = new PoolBookingRequest();
//        request.setUserId(userId);
//        request.setPoolSessionId(sessionId);
//
//        User user = new User();
//        user.setId(userId);
//        user.setFamilyId(familyId);
//
//        PoolSession session = new PoolSession();
//        session.setId(sessionId);
//        session.setSessionDate(LocalDate.now().plusDays(2));
//        session.setStartTime(LocalTime.of(14, 0));
//        session.setBookedCount(1);
//        session.setMaxCapacity(10);
//
//        PoolBooking initialBooking = new PoolBooking();
//
//        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(poolBookingRepository.existsByUserIdAndPoolSessionId(userId, sessionId)).thenReturn(false);
//        when(userRepository.countByFamilyId(familyId)).thenReturn(2);
//        when(poolBookingRepository.countBookingsForFamilyInWeek(eq(familyId), any(), any())).thenReturn(0L);
//        when(poolBookingMapper.toEntity(request)).thenReturn(initialBooking);
//
//        when(poolBookingRepository.save(initialBooking))
//                .thenThrow(new ObjectOptimisticLockingFailureException(PoolBooking.class, "id"));
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(SecurityUtils::currentFamilyId).thenReturn(familyId);
//
//            assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
//                poolBookingService.book(request);
//            });
//
//            verify(poolSessionRepository, times(1)).save(session);
//            verify(poolBookingRepository, times(1)).save(initialBooking);
//        }
//    }
//
//    @Test
//    void deleteBooking_ShouldThrowTimeOutException_WhenLessThan6HoursBeforeSession() {
//        UUID bookingId = UUID.randomUUID();
//        UUID sessionId = UUID.randomUUID();
//
//        PoolBooking booking = new PoolBooking();
//        booking.setPoolSessionId(sessionId);
//
//        LocalDateTime nearFuture = LocalDateTime.now().plusHours(2);
//
//        PoolSession session = new PoolSession();
//        session.setId(sessionId);
//        session.setSessionDate(nearFuture.toLocalDate());
//        session.setStartTime(nearFuture.toLocalTime());
//        session.setBookedCount(5);
//
//        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
//        when(poolSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(SecurityUtils::isAdmin).thenReturn(true);
//
//            FamilyException exception = assertThrows(FamilyException.class, () -> {
//                poolBookingService.deleteBooking(bookingId);
//            });
//
//            assertEquals(FamilyErrorCode.TIME_OUT, exception.getErrorCode());
//            assertEquals(5, session.getBookedCount());
//            verify(poolSessionRepository, never()).save(any());
//            verify(poolBookingRepository, never()).deleteById(any());
//        }
//    }
//}