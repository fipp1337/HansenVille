package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaHallRequest;
import com.hansenvillage.hansenapp.dto.CinemaHallResponse;
import com.hansenvillage.hansenapp.entity.CinemaHall;
import com.hansenvillage.hansenapp.mapper.CinemaHallMapper;
import com.hansenvillage.hansenapp.service.CinemaHallService;
import com.hansenvillage.hansenapp.service.CinemaSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cinema/hall")
@RequiredArgsConstructor
public class CinemaHallController {

    private final CinemaHallService cinemaHallService;
    private final CinemaHallMapper cinemaHallMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public CinemaHallResponse createHall(@Valid @RequestBody CinemaHallRequest request) {
        return cinemaHallService.create(request.getName());
    }
}
