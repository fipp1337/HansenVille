package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import com.hansenvillage.hansenapp.entity.CinemaSeat;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.CinemaBookingMapper;
import com.hansenvillage.hansenapp.repository.CinemaBookingRepository;
import com.hansenvillage.hansenapp.repository.CinemaSeatRepository;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityFamily;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CinemaBookingServiceTest {

    @Mock
    private CinemaBookingRepository cinemaBookingRepository;
    @Mock
    private CinemaSessionRepository cinemaSessionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CinemaBookingMapper cinemaBookingMapper;
    @Mock
    private CinemaSeatRepository cinemaSeatRepository;

    @InjectMocks
    CinemaBookingService cinemaBookingService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void book_shouldCreateBookingAndIncreaseBookedCount() {

        UUID familyId = UUID.randomUUID();

        CinemaBookingRequest request = new CinemaBookingRequest();
        request.setUserId(UUID.randomUUID());
        request.setCinemaSessionId(UUID.randomUUID());
        request.setSeatId(UUID.randomUUID());

        CinemaBooking booking = new CinemaBooking();

        CinemaSession session = new CinemaSession();
        session.setBookedCount(10);
        session.setMaxCapacity(20);

        CinemaSeat seat = new CinemaSeat();

        User user = new User();
        user.setFamilyId(familyId);

        SecurityFamily securityFamily = mock(SecurityFamily.class);
        when(securityFamily.getId()).thenReturn(familyId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityFamily);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(cinemaSessionRepository.findById(request.getCinemaSessionId())).thenReturn(Optional.of(session));
        when(cinemaSeatRepository.findById(request.getSeatId())).thenReturn(Optional.of(seat));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(cinemaBookingRepository.existsByCinemaSessionIdAndSeatId(request.getCinemaSessionId(), request.getSeatId())).thenReturn(false);
        when(cinemaSessionRepository.save(session)).thenReturn(session);
        when(cinemaBookingMapper.toEntity(request)).thenReturn(booking);
        when(cinemaBookingRepository.save(booking)).thenReturn(booking);

        cinemaBookingService.book(request);

        assertEquals(11, session.getBookedCount());

        verify(cinemaSessionRepository).findById(request.getCinemaSessionId());
        verify(cinemaSeatRepository).findById(request.getSeatId());
        verify(userRepository).findById(request.getUserId());
        verify(cinemaBookingRepository).existsByCinemaSessionIdAndSeatId(request.getCinemaSessionId(), request.getSeatId());
        verify(cinemaSessionRepository).save(session);
        verify(cinemaBookingMapper).toEntity(request);
        verify(cinemaBookingRepository).save(booking);
    }

    @Test
    void cinemaSessionIsFull_exception() {

        CinemaBookingRequest request = new CinemaBookingRequest();
        request.setUserId(UUID.randomUUID());
        request.setCinemaSessionId(UUID.randomUUID());
        request.setSeatId(UUID.randomUUID());

        CinemaSession session = new CinemaSession();
        session.setBookedCount(20);
        session.setMaxCapacity(20);

        when(cinemaSessionRepository.findById(request.getCinemaSessionId())).thenReturn(Optional.of(session));

        FamilyException exception = assertThrows(
                FamilyException.class,
                () -> cinemaBookingService.book(request)
        );

        assertEquals(FamilyErrorCode.CINEMA_SESSION_IS_FULL, exception.getErrorCode());
    }

    @Test
    void seatAlreadyBooked_exception() {

        UUID familyId = UUID.randomUUID();

        CinemaBookingRequest request = new CinemaBookingRequest();
        request.setUserId(UUID.randomUUID());
        request.setCinemaSessionId(UUID.randomUUID());
        request.setSeatId(UUID.randomUUID());

        CinemaSession session = new CinemaSession();
        session.setBookedCount(10);
        session.setMaxCapacity(20);

        CinemaSeat seat = new CinemaSeat();
        User user = new User();
        user.setFamilyId(familyId);

        SecurityFamily securityFamily = mock(SecurityFamily.class);
        when(securityFamily.getId()).thenReturn(familyId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityFamily);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(cinemaSessionRepository.findById(request.getCinemaSessionId())).thenReturn(Optional.of(session));
        when(cinemaSeatRepository.findById(request.getSeatId())).thenReturn(Optional.of(seat));
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(cinemaBookingRepository
                .existsByCinemaSessionIdAndSeatId(request.getCinemaSessionId(), request.getSeatId()))
                .thenReturn(true);

        FamilyException exception = assertThrows(
                FamilyException.class,
                () -> cinemaBookingService.book(request)
        );

        assertEquals(FamilyErrorCode.SEAT_ALREADY_BOOKED, exception.getErrorCode());
    }

    @Test
    void getBookingById() {

        UUID id = UUID.randomUUID();
        CinemaBooking booking = new CinemaBooking();

        when(cinemaBookingRepository.findById(id)).thenReturn(Optional.of(booking));
        CinemaBooking result = cinemaBookingService.getBookingById(id);

        assertEquals(booking, result);

        verify(cinemaBookingRepository).findById(id);
    }

    @Test
    void getAllBookings() {

        List<CinemaBooking> bookings = List.of(new CinemaBooking());

        when(cinemaBookingRepository.findAll()).thenReturn(bookings);

        List<CinemaBooking> results = cinemaBookingService.getAllBookings();

        assertEquals(bookings, results);

        verify(cinemaBookingRepository).findAll();
    }

    @Test
    void getBookingsByUserId() {

        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setFamilyId(familyId);

        List<CinemaBooking> bookings = List.of(new CinemaBooking());

        SecurityFamily securityFamily = mock(SecurityFamily.class);
        when(securityFamily.getId()).thenReturn(familyId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityFamily);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cinemaBookingRepository.findByUserId(userId)).thenReturn(bookings);

        List<CinemaBooking> results = cinemaBookingService.getBookingsByUserId(userId);

        assertEquals(bookings, results);

        verify(userRepository).findById(userId);
        verify(cinemaBookingRepository).findByUserId(userId);
    }

    @Test
    void getBookingsByFamilyId() {

        UUID familyId = UUID.randomUUID();
        SecurityFamily securityFamily = mock(SecurityFamily.class);
        when(securityFamily.getId()).thenReturn(familyId);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(securityFamily);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        List<CinemaBooking> bookings = List.of(new CinemaBooking());

        when(cinemaBookingRepository.findByFamilyId(familyId)).thenReturn(bookings);

        List<CinemaBooking> results = cinemaBookingService.getBookingsByFamilyId(familyId);

        assertEquals(bookings, results);

        verify(cinemaBookingRepository).findByFamilyId(familyId);
    }

    @Test
    void deleteBooking_shouldDeleteBookingAndDecreaseBookedCount() {

        UUID bookingId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        CinemaBooking booking = new CinemaBooking();
        booking.setCinemaSessionId(sessionId);

        CinemaSession session = new CinemaSession();
        session.setSessionDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.NOON);
        session.setBookedCount(5);

        when(cinemaBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(cinemaSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        cinemaBookingService.deleteBooking(bookingId);

        verify(cinemaSessionRepository).save(session);
        verify(cinemaBookingRepository).deleteById(bookingId);

        assertEquals(4, session.getBookedCount());
    }

    @Test
    void booking_notExist() {

        UUID id = UUID.randomUUID();

        when(cinemaBookingRepository.findById(id)).thenReturn(Optional.empty());

        FamilyException exception = assertThrows(
                FamilyException.class,
                () -> cinemaBookingService.deleteBooking(id)
        );

        assertEquals(
                FamilyErrorCode.BOOKING_NOT_FOUND,
                exception.getErrorCode()
        );
    }

    @Test
    void session_notExist() {

        UUID bookingId = UUID.randomUUID();
        CinemaBooking booking = new CinemaBooking();

        UUID sessionId = UUID.randomUUID();
        booking.setCinemaSessionId(sessionId);

        when(cinemaBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(cinemaSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        FamilyException exception = assertThrows(
                FamilyException.class,
                () -> cinemaBookingService.deleteBooking(bookingId)
        );

        assertEquals(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void less_than_6h() {

        UUID bookingId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        LocalDateTime sessionStart = LocalDateTime.now().plusHours(2);

        CinemaSession session = new CinemaSession();
        session.setSessionDate(sessionStart.toLocalDate());
        session.setStartTime(sessionStart.toLocalTime());

        CinemaBooking booking = new CinemaBooking();
        booking.setCinemaSessionId(sessionId);

        when(cinemaBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(cinemaSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        FamilyException exception = assertThrows(
                FamilyException.class,
                () -> cinemaBookingService.deleteBooking(bookingId)
        );

        assertEquals(FamilyErrorCode.TIME_OUT, exception.getErrorCode());
    }

    @Test
    void bookedCount_0() {

        UUID bookingId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        CinemaBooking booking = new CinemaBooking();
        booking.setCinemaSessionId(sessionId);

        CinemaSession session = new CinemaSession();
        session.setSessionDate(LocalDate.now().plusDays(1));
        session.setStartTime(LocalTime.NOON);
        session.setBookedCount(0);

        when(cinemaBookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(cinemaSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        cinemaBookingService.deleteBooking(bookingId);

        verify(cinemaSessionRepository, never())
                .save(any());

        verify(cinemaBookingRepository)
                .deleteById(bookingId);
    }
}