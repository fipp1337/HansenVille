package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.constant.AppConstant;
import com.hansenvillage.hansenapp.dto.CinemaPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionWithSeatsResponse;
import com.hansenvillage.hansenapp.dto.CinemaSeatWithAvailableResponse;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.CinemaSeatMapper;
import com.hansenvillage.hansenapp.mapper.CinemaSessionMapper;
import com.hansenvillage.hansenapp.repository.CinemaBookingRepository;
import com.hansenvillage.hansenapp.repository.CinemaSeatRepository;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaSessionService {

    private final CinemaSessionRepository cinemaSessionRepository;
    private final CinemaSessionMapper cinemaSessionMapper;
    private final CinemaSeatRepository cinemaSeatRepository;
    private final CinemaSeatMapper cinemaSeatMapper;
    private final CinemaBookingRepository cinemaBookingRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public List<CinemaSession> create(CinemaPublishWeekScheduleRequest request) {
        List<CinemaSession> sessions = cinemaSessionMapper.toEntityList(request);
        List<CinemaSession> saved = cinemaSessionRepository.saveAll(sessions);
        log.info("Cinema schedule published: sessions={}", saved.size());
        return saved;
    }

    @Transactional
    public void uploadPoster(UUID sessionId, MultipartFile file) {
        CinemaSession session = findSession(sessionId);
        session.setPosterImage(fileStorageService.store(file, AppConstant.Upload.POSTERS_DIR));
        cinemaSessionRepository.save(session);
        log.info("Cinema poster uploaded: session={}", sessionId);
    }

    @Transactional(readOnly = true)
    public Resource getPoster(UUID sessionId) {
        CinemaSession session = findSession(sessionId);
        return fileStorageService.loadAsResource(
                AppConstant.Upload.POSTERS_DIR,
                session.getPosterImage(),
                AppErrorCode.POSTER_NOT_FOUND
        );
    }

    @Transactional
    public void updatePoster(UUID sessionId, MultipartFile newFile) {
        deletePoster(sessionId);
        uploadPoster(sessionId, newFile);
    }

    @Transactional
    public void deletePoster(UUID sessionId) {
        CinemaSession session = findSession(sessionId);
        if (session.getPosterImage() == null) {
            throw AppException.of(AppErrorCode.POSTER_NOT_FOUND, sessionId);
        }

        fileStorageService.delete(AppConstant.Upload.POSTERS_DIR, session.getPosterImage());
        session.setPosterImage(null);
        cinemaSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public CinemaSessionWithSeatsResponse findById(UUID id) {
        CinemaSession session = findSession(id);
        CinemaSessionWithSeatsResponse response = cinemaSessionMapper.toResponseWithSeats(session);

        List<CinemaSeat> seats = cinemaSeatRepository.findByHallId(session.getHallId());
        List<UUID> bookedSeatIds = cinemaBookingRepository.findSeatIdsByCinemaSessionId(session.getId());

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

    @Transactional
    public CinemaSession update(UUID id, CinemaSessionRequest request) {
        CinemaSession session = findSession(id);
        cinemaSessionMapper.updateEntity(request, session);
        return cinemaSessionRepository.save(session);
    }

    @Transactional
    public void delete(UUID id) {
        if (!cinemaSessionRepository.existsById(id)) {
            throw AppException.of(AppErrorCode.CINEMA_SESSION_NOT_FOUND, id);
        }
        cinemaBookingRepository.deleteAll(cinemaBookingRepository.findByCinemaSessionId(id));
        cinemaSessionRepository.deleteById(id);
        log.info("Cinema session deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public List<CinemaSession> getWeekSchedule(LocalDate weekStart) {
        LocalDateTime weekStartDateTime = weekStart.atStartOfDay();
        LocalDateTime weekEndDateTime = weekStart.plusDays(6).atTime(LocalTime.MAX);

        return cinemaSessionRepository.findByStartAtBetween(weekStartDateTime, weekEndDateTime).stream()
                .sorted(Comparator.comparing(CinemaSession::getStartAt))
                .toList();
    }

    private CinemaSession findSession(UUID id) {
        return cinemaSessionRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.CINEMA_SESSION_NOT_FOUND, id));
    }

    @Transactional
    public void cancel(UUID id) {
        CinemaSession session = cinemaSessionRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.CINEMA_SESSION_NOT_FOUND));

        if (session.getStatus() == SessionStatus.CANCELLED) {
            return;
        }

        cinemaBookingRepository.deleteByCinemaSessionId(id);

        session.setStatus(SessionStatus.CANCELLED);
        log.info("Cinema session cancelled: {}", id);
    }

    @Transactional
    public void uncancel(UUID id) {
        CinemaSession session = cinemaSessionRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.CINEMA_SESSION_NOT_FOUND));

        if (session.getStatus() == SessionStatus.CANCELLED) {
            session.setStatus(SessionStatus.ACTIVE);
            cinemaSessionRepository.save(session);
            log.info("Cinema session restored: {}", id);
        }
    }
}
