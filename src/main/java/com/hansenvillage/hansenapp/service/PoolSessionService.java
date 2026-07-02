package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
import com.hansenvillage.hansenapp.mapper.PoolTemplateMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.PoolTemplateRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PoolSessionService {

    private final PoolSessionRepository poolSessionRepository;
    private final PoolTemplateRepository poolTemplateRepository;
    private final PoolTemplateMapper poolTemplateMapper;
    private final PoolSessionMapper poolSessionMapper;
    private final PoolBookingRepository poolBookingRepository;

    @Transactional
    public List<PoolSession> getAvailableSessionsForNextWeek(LocalDate fromDate) {
        LocalDate toDate = fromDate.plusDays(7);
        return new ArrayList<>();
    }

    public List<PoolSession> create(PoolPublishWeekScheduleRequest request) {
        List<PoolSession> sessions = poolSessionMapper.toEntityList(request);
        return poolSessionRepository.saveAll(sessions);
    }

    public PoolSession findById(UUID id) {

        return poolSessionRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, id));
    }

        public PoolSession update(UUID id, PoolSessionRequest request) {

            PoolSession session = poolSessionRepository.findById(id)
                    .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, id));

            poolSessionMapper.updateEntity(request, session);

            return poolSessionRepository.save(session);
        }

    @Transactional
    public void delete(UUID id) {
        if (!poolSessionRepository.existsById(id)) {
            throw FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, id);
        }
        poolBookingRepository.deleteByPoolSessionId(id);
        poolSessionRepository.deleteById(id);
    }


    public List<PoolSession> getWeekSchedule(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return poolSessionRepository.findBySessionDateBetween(weekStart, weekEnd).stream()
                .sorted(Comparator.comparing(PoolSession::getSessionDate)
                        .thenComparing(PoolSession::getStartTime))
                .toList();
    }
}
