package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionResponse;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
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
@RequestMapping("/api/pool/session")
@RequiredArgsConstructor
public class PoolSessionController {

    private final PoolSessionService poolSessionService;
    private final PoolSessionMapper poolSessionMapper;

    @GetMapping("/week")
    public List<PoolSessionResponse> getWeekSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return poolSessionMapper.toResponseList(poolSessionService.getWeekSchedule(weekStart));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public List<PoolSessionResponse> create(@Valid @RequestBody PoolPublishWeekScheduleRequest request) {
        return poolSessionMapper.toResponseList(poolSessionService.create(request));
    }

    @GetMapping("/{id}")
    public PoolSessionResponse getSessionInfoById(@PathVariable UUID id) {
        return poolSessionMapper.toResponse(poolSessionService.findById(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public PoolSessionResponse update(@PathVariable UUID id, @Valid @RequestBody PoolSessionRequest request) {
        return poolSessionMapper.toResponse(poolSessionService.update(id, request));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        poolSessionService.delete(id);
    }

    @PutMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void cancelSession(@PathVariable UUID id) {
        poolSessionService.cancel(id);
    }

    @PutMapping("/cancel-by-day")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void cancelSessionsForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        poolSessionService.cancelSessionsForDay(date);
    }

    @PutMapping("/{id}/uncancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void uncancelSession(@PathVariable UUID id) {
        poolSessionService.uncancelSession(id);
    }

    @PutMapping("/uncancel-by-day")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void uncancelSessionsForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        poolSessionService.uncancelSessionsForDay(date);
    }
}
