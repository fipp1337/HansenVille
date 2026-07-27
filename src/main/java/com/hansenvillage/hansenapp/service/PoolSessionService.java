package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PoolSessionService {

    private final PoolSessionRepository poolSessionRepository;
    private final PoolSessionMapper poolSessionMapper;
    private final PoolBookingRepository poolBookingRepository;

    @Transactional
    public List<PoolSession> getAvailableSessionsForNextWeek(LocalDate fromDate) {
        LocalDate toDate = fromDate.plusDays(7);
        return poolSessionRepository.findBySessionDateBetween(fromDate, toDate).stream()
                .filter(s -> s.getStatus() != SessionStatus.CANCELLED)
                .filter(s -> s.getBookedCount() < s.getMaxCapacity())
                .sorted(Comparator.comparing(PoolSession::getSessionDate)
                        .thenComparing(PoolSession::getStartTime))
                .toList();
    }

    public List<PoolSession> create(PoolPublishWeekScheduleRequest request) {
        List<PoolSession> sessions = poolSessionMapper.toEntityList(request);
        return poolSessionRepository.saveAll(sessions);
    }

    public PoolSession findById(UUID id) {
        return poolSessionRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, id));
    }

    @Transactional
    public PoolSession update(UUID id, PoolSessionRequest request) {
        PoolSession session = findById(id);
        poolSessionMapper.updateEntity(request, session);
        return poolSessionRepository.save(session);
    }

    @Transactional
    public void delete(UUID id) {
        PoolSession session = findById(id);
        List<PoolBooking> bookings = poolBookingRepository.findByPoolSessionId(id);
        for (PoolBooking booking : bookings) {
            booking.setStatus(PoolBookingStatus.CANCELED_WITH_RETURN);
        }
        poolBookingRepository.saveAll(bookings);
        poolSessionRepository.delete(session);
    }

    @Transactional
    public void cancel(UUID id) {
        PoolSession session = findById(id);

        if (session.getStatus() == SessionStatus.CANCELLED) {
            return;
        }

        List<PoolBooking> bookings = poolBookingRepository.findByPoolSessionId(id);

        for (PoolBooking booking : bookings) {
            if (booking.getStatus() == PoolBookingStatus.REGISTERED) {
                booking.setStatus(PoolBookingStatus.CANCELED_WITH_RETURN);
            }
        }

        session.setStatus(SessionStatus.CANCELLED);
        session.setBookedCount(0);
        poolSessionRepository.save(session);
    }



    @Transactional
    public void cancelSessionsForDay(LocalDate date) {
        List<UUID> sessionIds = poolSessionRepository.findIdsByDate(date);
        for (UUID id : sessionIds) {
            cancel(id);
        }
    }

    @Transactional
    public void unCancelSession(UUID id) {
        PoolSession session = findById(id);

        if (session.getStatus() == SessionStatus.CANCELLED) {
            session.setStatus(SessionStatus.ACTIVE);
            poolSessionRepository.save(session);
        }
    }

    @Transactional
    public void unCancelSessionsForDay(LocalDate date) {
        List<UUID> sessionIds = poolSessionRepository.findIdsByDate(date);
        for (UUID id : sessionIds) {
            unCancelSession(id);
        }
    }

    public List<PoolSession> getWeekSchedule(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return poolSessionRepository.findBySessionDateBetween(weekStart, weekEnd).stream()
                .sorted(Comparator.comparing(PoolSession::getSessionDate)
                        .thenComparing(PoolSession::getStartTime))
                .toList();
    }
}
