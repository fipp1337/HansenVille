package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.service.CinemaSessionService;
import com.hansenvillage.hansenapp.service.PoolSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cinema/session")
@RequiredArgsConstructor
public class CinemaSessionController {

    private final CinemaSessionService cinemaSessionService;

    @GetMapping("/week")
    public List<CinemaSession> getWeekSchedule(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                             LocalDate weekStart) {
        return cinemaSessionService.getWeekSchedule(weekStart);
    }

    @PostMapping
    public ResponseEntity<List<CinemaSession>> create(@Valid @RequestBody CinemaPublishWeekScheduleRequest request) {
        List<CinemaSession> sessions = cinemaSessionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessions);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public CinemaSession getById(@PathVariable UUID id) {
        return cinemaSessionService.findById(id);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public CinemaSession update(@PathVariable UUID id, @Valid @RequestBody CinemaSessionRequest request) {
        return cinemaSessionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        cinemaSessionService.delete(id);
    }
}
