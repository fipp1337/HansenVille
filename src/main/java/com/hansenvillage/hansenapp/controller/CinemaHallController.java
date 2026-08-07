package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaHallRequest;
import com.hansenvillage.hansenapp.dto.CinemaHallResponse;
import com.hansenvillage.hansenapp.service.CinemaHallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cinema/halls")
@RequiredArgsConstructor
public class CinemaHallController {

    private final CinemaHallService cinemaHallService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public CinemaHallResponse createHall(@Valid @RequestBody CinemaHallRequest request) {
        return cinemaHallService.create(request.getName());
    }
}
