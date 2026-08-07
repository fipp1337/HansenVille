package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.MessageResponse;
import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolSessionResponse;
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
@RequestMapping("/api/pool/sessions")
@RequiredArgsConstructor
public class PoolSessionController {

    private final PoolSessionService poolSessionService;
    private final PoolSessionMapper poolSessionMapper;

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

    @GetMapping("/week")
    public List<PoolSessionResponse> getWeekSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return poolSessionMapper.toResponseList(poolSessionService.getWeekSchedule(weekStart));
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

    @PostMapping("/{id}/cancel")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public MessageResponse cancelSession(@PathVariable UUID id) {
        poolSessionService.cancelSession(id);
        return new MessageResponse("Pool session cancelled successfully");
    }

    @PostMapping("/{id}/uncancel")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public MessageResponse uncancelSession(@PathVariable UUID id) {
        poolSessionService.uncancelSession(id);
        return new MessageResponse("Pool session restored successfully");
    }

    @PostMapping("/cancel-by-day")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public MessageResponse cancelByDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        poolSessionService.cancelSessionsForDay(date);
        return new MessageResponse("All pool sessions for " + date + " cancelled successfully");
    }

    @PostMapping("/uncancel-by-day")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public MessageResponse uncancelByDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        poolSessionService.uncancelSessionsForDay(date);
        return new MessageResponse("All pool sessions for " + date + " restored successfully");
    }
}