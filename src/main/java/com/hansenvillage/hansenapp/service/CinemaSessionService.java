package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.CinemaSeat;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.CinemaSeatMapper;
import com.hansenvillage.hansenapp.mapper.CinemaSessionMapper;
import com.hansenvillage.hansenapp.repository.CinemaBookingRepository;
import com.hansenvillage.hansenapp.repository.CinemaSeatRepository;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaSessionService {

    private final CinemaSessionRepository cinemaSessionRepository;
    private final CinemaSessionMapper cinemaSessionMapper;
    private final CinemaSeatRepository cinemaSeatRepository;
    private final CinemaSeatMapper cinemaSeatMapper;
    private final CinemaBookingRepository cinemaBookingRepository;

    public List<CinemaSession> create(CinemaPublishWeekScheduleRequest request) {
        List<CinemaSession> sessions = cinemaSessionMapper.toEntityList(request);
        return cinemaSessionRepository.saveAll(sessions);
    }

    public CinemaSessionWithSeatsResponse findById(UUID id) {

        CinemaSession session = cinemaSessionRepository.findById(id)
                .orElseThrow(() ->
                        FamilyException.of(
                                FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, id));

        CinemaSessionWithSeatsResponse response = cinemaSessionMapper.toResponseWithSeats(session);

        List<CinemaSeat> seats = cinemaSeatRepository.findByHallId(session.getHallId());

        List<UUID> bookedSeatIds = cinemaBookingRepository
                .findSeatIdsByCinemaSessionId(session.getId());

        List<CinemaSeatWithAvailableResponse> seatResponses = seats.stream()
                .map(seat -> {
                    CinemaSeatWithAvailableResponse seatResponse = cinemaSeatMapper.toResponseWithAvailable(seat);
                    seatResponse.setAvailable(!bookedSeatIds.contains(seat.getId()));

                    return seatResponse;
                })
                .toList();

        response.setSeats(seatResponses);

        return response;
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

        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();
        LocalDateTime weekEndDateTime = weekStart.plusDays(6).atTime(LocalTime.MAX);

        return cinemaSessionRepository.findByStartAtBetween(weekStartDateTime, weekEndDateTime)
                    .stream()
                    .sorted(Comparator.comparing(CinemaSession::getStartAt))
                    .toList();
    }
}
