package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionResponse;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
import com.hansenvillage.hansenapp.service.PoolSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pool/session")
@RequiredArgsConstructor
public class PoolSessionController {
    private final PoolSessionService poolSessionService;
    private final PoolSessionMapper poolSessionMapper;

    @GetMapping("/week")
    public List<PoolSessionResponse> getWeekSchedule(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        List<PoolSession> schedule = poolSessionService.getWeekSchedule(weekStart);
        return poolSessionMapper.toResponseList(schedule);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public List<PoolSessionResponse> create(@Valid @RequestBody PoolPublishWeekScheduleRequest request) {
        List<PoolSession> sessions = poolSessionService.create(request);
        return poolSessionMapper.toResponseList(sessions);
    }

    @GetMapping("/{id}")
    public PoolSessionResponse getSessionInfoById(@PathVariable UUID id) {
        PoolSession session = poolSessionService.findById(id);
        return poolSessionMapper.toResponse(session);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public PoolSessionResponse update(@PathVariable UUID id, @Valid @RequestBody PoolSessionRequest request) {
        PoolSession updated = poolSessionService.update(id, request);
        return poolSessionMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        poolSessionService.delete(id);
    }

    @PutMapping("/cancel/{id}")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void cancel(@PathVariable UUID id) {
        poolSessionService.cancel(id);
    }

    @PutMapping("/cancel/date/{date}")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void cancelFullDay(@PathVariable LocalDate date) {
        poolSessionService.cancelSessionsForDay(date);
    }
}