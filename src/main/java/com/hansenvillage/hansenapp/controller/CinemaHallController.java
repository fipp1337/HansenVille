package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaHallRequest;
import com.hansenvillage.hansenapp.dto.CinemaHallResponse;
import com.hansenvillage.hansenapp.mapper.CinemaHallMapper;
import com.hansenvillage.hansenapp.service.CinemaHallService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cinema/halls")
@RequiredArgsConstructor
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
public class CinemaHallController {

    private final CinemaHallService cinemaHallService;
    private final CinemaHallMapper cinemaHallMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CinemaHallResponse createHall(@Valid @RequestBody CinemaHallRequest request) {
        return cinemaHallService.create(request.getName());
    }

    @GetMapping("/{hallId}/seats")
    public CinemaHallResponse getHallWithSeats(@PathVariable UUID hallId) {
        return cinemaHallService.findById(hallId);
    }

    @PutMapping("/{hallId}")
    public CinemaHallResponse updateHall(@PathVariable UUID hallId, @Valid @RequestBody CinemaHallRequest request) {
        return cinemaHallMapper.toResponse(cinemaHallService.update(hallId, request));
    }

    @DeleteMapping("/{hallId}")
    public void deleteHall(@PathVariable UUID hallId) {
        cinemaHallService.delete(hallId);
    }

}
