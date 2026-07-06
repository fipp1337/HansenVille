package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionResponse;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
import com.hansenvillage.hansenapp.service.PoolBookingService;
import com.hansenvillage.hansenapp.service.PoolSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.springframework.boot.origin.OriginTrackedValue.of;

@RestController
@RequestMapping("/api/pool/session")
@RequiredArgsConstructor
public class PoolSessionController {
    private final PoolSessionService poolSessionService;
    private final PoolBookingService poolBookingService;
    private final PoolSessionMapper poolSessionMapper;

    @GetMapping("/week")
    public List<PoolSessionResponse> getWeekSchedule(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        List<PoolSession> schedule = poolSessionService.getWeekSchedule(weekStart);
        return poolSessionMapper.toResponseList(schedule);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<PoolSessionResponse> create(@Valid @RequestBody PoolPublishWeekScheduleRequest request) {
        List<PoolSession> sessions = poolSessionService.create(request);
        return poolSessionMapper.toResponseList(sessions);
    }

    @GetMapping("/bookings/{id}")
    public List<PoolBookingResponse> getBooksBySession(@PathVariable("id") UUID id) {
        return poolBookingService.getBookingDetailsForSession(id);
    }

    @GetMapping("/{id}")
    public PoolSessionResponse getSessionInfoById(@PathVariable UUID id) {
        PoolSession session = poolSessionService.findById(id);
        return poolSessionMapper.toResponse(session);
    }

    @PutMapping("/{id}")
    public PoolSessionResponse update(@PathVariable UUID id, @Valid @RequestBody PoolSessionRequest request) {
        PoolSession updated = poolSessionService.update(id, request);
        return poolSessionMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        poolSessionService.delete(id);
    }
}