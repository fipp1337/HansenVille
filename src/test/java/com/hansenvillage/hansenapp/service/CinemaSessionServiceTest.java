package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionRequest;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.CinemaSessionMapper;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @InjectMocks
    CinemaSessionService cinemaSessionService;

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

        UUID id = UUID.randomUUID();
        CinemaSession session = new CinemaSession();

        when(cinemaSessionRepository.findById(id)).thenReturn(Optional.of(session));

        CinemaSession result = cinemaSessionService.findById(id);

        assertEquals(session, result);

        verify(cinemaSessionRepository).findById(id);
    }

    @Test
    void cinemaSessionNotFound_exception() {

        UUID id = UUID.randomUUID();

        when(cinemaSessionRepository.findById(id)).thenReturn(Optional.empty());

        FamilyException exception = assertThrows(FamilyException.class,
                () -> cinemaSessionService.findById(id));

        assertEquals(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, exception.getErrorCode());

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
        LocalDate weekEnd = weekStart.plusDays(6);

        CinemaSession first = new CinemaSession();
        first.setSessionDate(LocalDate.of(2026, 7, 5));
        first.setStartTime(LocalTime.of(18, 0));

        CinemaSession second = new CinemaSession();
        second.setSessionDate(LocalDate.of(2026, 7, 3));
        second.setStartTime(LocalTime.of(10, 0));

        List<CinemaSession> unsorted = List.of(first, second);

        when(cinemaSessionRepository.findBySessionDateBetween(weekStart, weekEnd)).thenReturn(unsorted);

        List<CinemaSession> result = cinemaSessionService.getWeekSchedule(weekStart);

        assertEquals(List.of(second, first), result);

        verify(cinemaSessionRepository).findBySessionDateBetween(weekStart, weekEnd);
    }
}