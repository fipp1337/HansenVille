package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.CinemaSeat;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.CinemaSeatMapper;
import com.hansenvillage.hansenapp.mapper.CinemaSessionMapper;
import com.hansenvillage.hansenapp.repository.CinemaBookingRepository;
import com.hansenvillage.hansenapp.repository.CinemaHallRepository;
import com.hansenvillage.hansenapp.repository.CinemaSeatRepository;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CinemaSessionServiceTest {

    @Mock
    private CinemaSessionRepository cinemaSessionRepository;
    @Mock
    private CinemaSessionMapper cinemaSessionMapper;
    @Mock
    private CinemaSeatMapper cinemaSeatMapper;
    @Mock
    private CinemaSeatRepository cinemaSeatRepository;
    @Mock
    private CinemaBookingRepository cinemaBookingRepository;

    @InjectMocks
    CinemaSessionService cinemaSessionService;
    private CinemaSeat seat2;

    @Test
    void create() {

        CinemaPublishWeekScheduleRequest request = new CinemaPublishWeekScheduleRequest();

        List<CinemaSession> sessions = List.of(new CinemaSession());

        when(cinemaSessionMapper.toEntityList(request)).thenReturn(sessions);
        when(cinemaSessionRepository.saveAll(sessions)).thenReturn(sessions);

        List<CinemaSession> results = cinemaSessionService.create(request);

        assertEquals(sessions, results);

        verify(cinemaSessionMapper).toEntityList(request);
        verify(cinemaSessionRepository).saveAll(sessions);
    }

    @Test
    void findById() {

        UUID sessionId = UUID.randomUUID();
        CinemaSession session = new CinemaSession();
        session.setId(sessionId);
        session.setHallId(UUID.randomUUID());

        CinemaSeat seat1 = new CinemaSeat();
        UUID seatId1 = UUID.randomUUID();
        seat1.setId(seatId1);
        seat1.setHallId(session.getHallId());
        seat1.setSeatNumber("A1");

        CinemaSeat seat2 = new CinemaSeat();
        UUID seatId2 = UUID.randomUUID();
        seat2.setId(seatId2);
        seat2.setHallId(session.getHallId());
        seat2.setSeatNumber("A2");

        CinemaSeat seat3 = new CinemaSeat();
        UUID seatId3 = UUID.randomUUID();
        seat3.setId(seatId3);
        seat3.setHallId(session.getHallId());
        seat3.setSeatNumber("B3");

        CinemaSeatWithAvailableResponse response1 = new CinemaSeatWithAvailableResponse();
        response1.setId(seatId1);
        CinemaSeatWithAvailableResponse response2 = new CinemaSeatWithAvailableResponse();
        response2.setId(seatId2);
        CinemaSeatWithAvailableResponse response3 = new CinemaSeatWithAvailableResponse();
        response3.setId(seatId3);

        List<CinemaSeat> seats = List.of(seat1, seat2, seat3);
        List<UUID> bookedSeatIds = List.of(seatId1, seatId2);

        CinemaSessionWithSeatsResponse sessionResponse = new CinemaSessionWithSeatsResponse();
        sessionResponse.setId(sessionId);

        when(cinemaSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(cinemaSessionMapper.toResponseWithSeats(session)).thenReturn(sessionResponse);
        when(cinemaSeatRepository.findByHallId(session.getHallId())).thenReturn(seats);
        when(cinemaBookingRepository.findSeatIdsByCinemaSessionId(session.getId())).thenReturn(bookedSeatIds);
        when(cinemaSeatMapper.toResponseWithAvailable(seat1)).thenReturn(response1);
        when(cinemaSeatMapper.toResponseWithAvailable(seat2)).thenReturn(response2);
        when(cinemaSeatMapper.toResponseWithAvailable(seat3)).thenReturn(response3);

        CinemaSessionWithSeatsResponse result = cinemaSessionService.findById(sessionId);

        assertEquals(3, result.getSeats().size());
        assertFalse(result.getSeats().get(0).isAvailable());
        assertFalse(result.getSeats().get(1).isAvailable());
        assertTrue(result.getSeats().get(2).isAvailable());

        verify(cinemaSessionRepository).findById(sessionId);
        verify(cinemaSessionMapper).toResponseWithSeats(session);
        verify(cinemaSeatRepository).findByHallId(session.getHallId());
        verify(cinemaBookingRepository).findSeatIdsByCinemaSessionId(session.getId());
    }

    @Test
    void cinemaSessionNotFound_exception() {

        UUID id = UUID.randomUUID();

        when(cinemaSessionRepository.findById(id)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> cinemaSessionService.findById(id));

        assertEquals(AppErrorCode.CINEMA_SESSION_NOT_FOUND, exception.getErrorCode());

        verify(cinemaSessionRepository).findById(id);
    }

    @Test
    void update_shouldUpdateAndSaveSession() {

        UUID id = UUID.randomUUID();
        CinemaSessionRequest request = new CinemaSessionRequest();
        CinemaSession session = new CinemaSession();

        when(cinemaSessionRepository.findById(id)).thenReturn(Optional.of(session));
        when(cinemaSessionRepository.save(session)).thenReturn(session);

        CinemaSession updated = cinemaSessionService.update(id, request);

        assertSame(session, updated);

        verify(cinemaSessionRepository).findById(id);
        verify(cinemaSessionMapper).updateEntity(request, session);
        verify(cinemaSessionRepository).save(session);
    }

    @Test
    void delete() {

        UUID id = UUID.randomUUID();

        when(cinemaSessionRepository.existsById(id)).thenReturn(true);

        cinemaSessionService.delete(id);

        verify(cinemaSessionRepository).existsById(id);
        verify(cinemaSessionRepository).deleteById(id);
    }

    @Test
    void getWeekSchedule_shouldReturnSessionsSortedByDateAndTime() {

        LocalDate weekStart = LocalDate.now();
        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();
        LocalDateTime weekEndDateTime = weekStart.plusDays(6).atTime(LocalTime.MAX);

        CinemaSession first = new CinemaSession();
        first.setStartAt(weekStart.plusDays(5).atTime(18, 0));

        CinemaSession second = new CinemaSession();
        second.setStartAt(weekStart.plusDays(1).atTime(10, 0));

        CinemaSession third = new CinemaSession();
        third.setStartAt(weekStart.plusDays(3).atTime(14, 30));

        List<CinemaSession> unsorted = List.of(first, third, second);

        when(cinemaSessionRepository.findByStartAtBetween(
                weekStartDateTime,
                weekEndDateTime))
                .thenReturn(unsorted);

        List<CinemaSession> result =
                cinemaSessionService.getWeekSchedule(weekStart);

        assertEquals(List.of(second, third, first), result);
    }
}