package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PoolSessionServiceTest {

    @Mock private PoolSessionRepository poolSessionRepository;
    @Mock private PoolSessionMapper poolSessionMapper;
    @Mock private PoolBookingRepository poolBookingRepository;

    @InjectMocks
    private PoolSessionService poolSessionService;

    @Test
    void getAvailableSessionsForNextWeek_ShouldReturnEmptyList() {
        List<PoolSession> result = poolSessionService.getAvailableSessionsForNextWeek(LocalDate.now());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void create_ShouldSaveAndReturnSessions() {
        PoolPublishWeekScheduleRequest request = new PoolPublishWeekScheduleRequest();
        List<PoolSession> mappedSessions = List.of(new PoolSession(), new PoolSession());

        when(poolSessionMapper.toEntityList(request)).thenReturn(mappedSessions);
        when(poolSessionRepository.saveAll(mappedSessions)).thenReturn(mappedSessions);

        List<PoolSession> result = poolSessionService.create(request);

        assertEquals(2, result.size());
        verify(poolSessionRepository, times(1)).saveAll(mappedSessions);
    }


    @Test
    void findById_ShouldReturnSession_WhenExists() {
        UUID id = UUID.randomUUID();
        PoolSession session = new PoolSession();
        when(poolSessionRepository.findById(id)).thenReturn(Optional.of(session));

        PoolSession result = poolSessionService.findById(id);

        assertNotNull(result);
        verify(poolSessionRepository).findById(id);
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(poolSessionRepository.findById(id)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> poolSessionService.findById(id));
        assertEquals(AppErrorCode.POOL_SESSION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void update_ShouldUpdateAndSaveSession_WhenExists() {
        UUID id = UUID.randomUUID();
        PoolSessionRequest request = new PoolSessionRequest();
        PoolSession existingSession = new PoolSession();

        when(poolSessionRepository.findById(id)).thenReturn(Optional.of(existingSession));

        when(poolSessionRepository.save(existingSession)).thenReturn(existingSession);

        PoolSession result = poolSessionService.update(id, request);

        assertNotNull(result);
        verify(poolSessionMapper).updateEntity(request, existingSession);
        verify(poolSessionRepository).save(existingSession);
    }


    @Test
    void delete_ShouldCancelBookingsAndDeleteSession_WhenExists() {
        UUID id = UUID.randomUUID();
        PoolSession session = new PoolSession();
        session.setId(id);

        when(poolSessionRepository.findById(id)).thenReturn(Optional.of(session));
        when(poolBookingRepository.findByPoolSessionId(id)).thenReturn(Collections.emptyList());

        poolSessionService.delete(id);

        verify(poolBookingRepository).findByPoolSessionId(id);
        verify(poolBookingRepository).saveAll(Collections.emptyList());
        verify(poolSessionRepository).delete(session);
    }

    @Test
    void delete_ShouldThrowException_WhenSessionDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(poolSessionRepository.findById(id)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> poolSessionService.delete(id));
        assertEquals(AppErrorCode.POOL_SESSION_NOT_FOUND, exception.getErrorCode());

        verify(poolBookingRepository, never()).findByPoolSessionId(any());
        verify(poolSessionRepository, never()).delete(any());
    }


    @Test
    void getWeekSchedule_ShouldReturnSortedSessions() {
        LocalDate start = LocalDate.of(2026, 7, 6);
        LocalDate end = start.plusDays(6);

        PoolSession s1 = new PoolSession();
        s1.setSessionDate(start.plusDays(2));
        s1.setStartTime(LocalTime.of(10, 0));

        PoolSession s2 = new PoolSession();
        s2.setSessionDate(start.plusDays(1));
        s2.setStartTime(LocalTime.of(15, 0));

        PoolSession s3 = new PoolSession();
        s3.setSessionDate(start.plusDays(1));
        s3.setStartTime(LocalTime.of(9, 0));

        when(poolSessionRepository.findBySessionDateBetween(start, end))
                .thenReturn(List.of(s1, s2, s3));

        List<PoolSession> result = poolSessionService.getWeekSchedule(start);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(s3, result.get(0));
        assertEquals(s2, result.get(1));
        assertEquals(s1, result.get(2));
    }
}