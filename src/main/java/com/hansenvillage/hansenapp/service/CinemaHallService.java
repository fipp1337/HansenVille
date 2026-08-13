package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaHallRequest;
import com.hansenvillage.hansenapp.dto.CinemaHallResponse;
import com.hansenvillage.hansenapp.dto.CinemaSeatResponse;
import com.hansenvillage.hansenapp.entity.CinemaHall;
import com.hansenvillage.hansenapp.entity.CinemaSeat;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.CinemaHallMapper;
import com.hansenvillage.hansenapp.mapper.CinemaSeatMapper;
import com.hansenvillage.hansenapp.repository.CinemaHallRepository;
import com.hansenvillage.hansenapp.repository.CinemaSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaHallService {

    private final CinemaSeatService cinemaSeatService;
    private final CinemaHallMapper cinemaHallMapper;
    private final CinemaSeatMapper cinemaSeatMapper;
    private final CinemaHallRepository cinemaHallRepository;
    private final CinemaSeatRepository cinemaSeatRepository;

    @Transactional
    public CinemaHallResponse create(String name) {
        CinemaHall hall = cinemaHallMapper.toEntity(name);
        cinemaHallRepository.save(hall);

        List<CinemaSeat> seats = cinemaSeatService.generateSeats(hall.getId());
        CinemaHallResponse response = cinemaHallMapper.toResponse(hall);
        response.setSeats(cinemaSeatMapper.toResponseList(seats));
        log.info("Cinema hall created: id={}, name={}, seats={}", hall.getId(), name, seats.size());
        return response;
    }

    public CinemaHallResponse findById(UUID id) {
        CinemaHall hall = cinemaHallRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.HALL_NOT_FOUND));

        List<CinemaSeatResponse> seats = cinemaSeatRepository.findByHallId(id)
                .stream()
                .map(cinemaSeatMapper::toResponse)
                .toList();

        CinemaHallResponse response = cinemaHallMapper.toResponse(hall);
        response.setSeats(seats);

        return response;
    }

    @Transactional
    public CinemaHall update(UUID hallId, CinemaHallRequest request) {
        CinemaHall hall = cinemaHallRepository.findById(hallId)
                .orElseThrow(() -> AppException.of(AppErrorCode.HALL_NOT_FOUND));
        cinemaHallMapper.updateEntity(request, hall);
        return cinemaHallRepository.save(hall);
    }

    @Transactional
    public void delete(UUID hallId) {
        CinemaHall hall = cinemaHallRepository.findById(hallId)
                .orElseThrow(() -> AppException.of(AppErrorCode.HALL_NOT_FOUND));
        List<CinemaSeat> seats = cinemaSeatRepository.findByHallId(hallId);
        cinemaSeatRepository.deleteAll(seats);
        cinemaHallRepository.delete(hall);
    }
}
