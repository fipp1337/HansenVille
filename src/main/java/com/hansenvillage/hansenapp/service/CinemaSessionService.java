package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionRequest;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.CinemaSessionMapper;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaSessionService {

    private final CinemaSessionRepository cinemaSessionRepository;
    private final CinemaSessionMapper cinemaSessionMapper;

    public List<CinemaSession> create(CinemaPublishWeekScheduleRequest request) {
        List<CinemaSession> sessions = cinemaSessionMapper.toEntityList(request);
        return cinemaSessionRepository.saveAll(sessions);
    }

    public CinemaSession findById(UUID id) {

        return cinemaSessionRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, id));
    }

    public CinemaSession update(UUID id, CinemaSessionRequest request) {

        CinemaSession session = cinemaSessionRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, id));

        cinemaSessionMapper.updateEntity(request, session);

        return cinemaSessionRepository.save(session);
    }

    @Transactional
    public void delete(UUID id) {
        if (!cinemaSessionRepository.existsById(id)) {
            throw FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, id);
        }
        cinemaSessionRepository.deleteById(id);
    }


    public List<CinemaSession> getWeekSchedule(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        return cinemaSessionRepository.findBySessionDateBetween(weekStart, weekEnd).stream()
                .sorted(Comparator.comparing(CinemaSession::getSessionDate)
                        .thenComparing(CinemaSession::getStartTime))
                .toList();
    }
}
