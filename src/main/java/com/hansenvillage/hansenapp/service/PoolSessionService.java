package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolBookingStatus;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolSessionService {

    private final PoolSessionRepository poolSessionRepository;
    private final PoolSessionMapper poolSessionMapper;
    private final PoolBookingRepository poolBookingRepository;

    @Transactional(readOnly = true)
    public List<PoolSession> getAvailableSessionsForNextWeek(LocalDate fromDate) {
        LocalDate toDate = fromDate.plusDays(7);
        return poolSessionRepository.findBySessionDateBetween(fromDate, toDate).stream()
                .filter(session -> session.getStatus() != SessionStatus.CANCELLED)
                .filter(session -> session.getBookedCount() < session.getMaxCapacity())
                .sorted(Comparator.comparing(PoolSession::getSessionDate)
                        .thenComparing(PoolSession::getStartTime))
                .toList();
    }

    @Transactional
    public List<PoolSession> create(PoolPublishWeekScheduleRequest request) {
        List<PoolSession> sessions = poolSessionMapper.toEntityList(request);
        List<PoolSession> saved = poolSessionRepository.saveAll(sessions);
        log.info("Pool schedule published: sessions={}", saved.size());
        return saved;
    }

    @Transactional(readOnly = true)
    public PoolSession findById(UUID id) {
        return poolSessionRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.POOL_SESSION_NOT_FOUND, id));
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
        if (!bookings.isEmpty()) {
            poolBookingRepository.deleteAll(bookings);
        }

        poolSessionRepository.delete(session);

        log.info("Pool session physically deleted: {}", id);
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
        log.info("Pool session cancelled: {}", id);
    }

    @Transactional
    public void cancelSessionsForDay(LocalDate date) {
        poolSessionRepository.findIdsByDate(date).forEach(this::cancel);
        log.info("Pool sessions cancelled for day: {}", date);
    }

    @Transactional
    public void uncancelSession(UUID id) {
        PoolSession session = findById(id);
        if (session.getStatus() == SessionStatus.CANCELLED) {
            session.setStatus(SessionStatus.ACTIVE);
            poolSessionRepository.save(session);
            log.info("Pool session restored: {}", id);
        }
    }

    @Transactional
    public void uncancelSessionsForDay(LocalDate date) {
        poolSessionRepository.findIdsByDate(date).forEach(this::uncancelSession);
    }

    @Transactional(readOnly = true)
    public List<PoolSession> getWeekSchedule(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return poolSessionRepository.findBySessionDateBetween(weekStart, weekEnd).stream()
                .sorted(Comparator.comparing(PoolSession::getSessionDate)
                        .thenComparing(PoolSession::getStartTime))
                .toList();
    }
}
