package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaHallRequest;
import com.hansenvillage.hansenapp.dto.CinemaHallResponse;
import com.hansenvillage.hansenapp.dto.CinemaSeatResponse;
import com.hansenvillage.hansenapp.entity.CinemaHall;
import com.hansenvillage.hansenapp.entity.CinemaSeat;
import com.hansenvillage.hansenapp.mapper.CinemaHallMapper;
import com.hansenvillage.hansenapp.mapper.CinemaSeatMapper;
import com.hansenvillage.hansenapp.repository.CinemaHallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaHallService {

    private final CinemaSeatService cinemaSeatService;
    private final CinemaHallMapper cinemaHallMapper;
    private final CinemaSeatMapper cinemaSeatMapper;
    private final CinemaHallRepository cinemaHallRepository;

    @Transactional
    public CinemaHallResponse create(String name) {

        CinemaHall hall = cinemaHallMapper.toEntity(name);
        cinemaHallRepository.save(hall);

        List<CinemaSeat> seats = cinemaSeatService.generateSeats(hall.getId());

        CinemaHallResponse response = cinemaHallMapper.toResponse(hall);
        response.setSeats(cinemaSeatMapper.toResponseList(seats));

        return response;
    }
}
