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
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    @Transactional
    public void uploadPoster(UUID sessionId, MultipartFile file) {

        try {
            CinemaSession session = cinemaSessionRepository.findById(sessionId)
                    .orElseThrow(() -> FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, sessionId));

            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            Path uploadDir = Paths.get("uploads/posters");

            Files.createDirectories(uploadDir);

            Files.copy(
                    file.getInputStream(),
                    uploadDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            session.setPosterImage(fileName);

            cinemaSessionRepository.save(session);
        } catch (IOException e) {
            throw FamilyException.of(FamilyErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public ResponseEntity<Resource> getPoster(UUID sessionId) {

        CinemaSession session = cinemaSessionRepository.findById(sessionId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, sessionId));

        Path path = Paths.get("uploads/posters")
                .resolve(session.getPosterImage());

        try {

            Resource resource = new UrlResource(path.toUri());

            String contentType = Files.probeContentType(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {

            throw FamilyException.of(FamilyErrorCode.POSTER_NOT_FOUND);
        }
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
