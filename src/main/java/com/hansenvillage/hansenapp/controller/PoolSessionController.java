package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
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

    @GetMapping("/week")
    public List<PoolSession> getWeekSchedule(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                 LocalDate weekStart) {
        return poolSessionService.getWeekSchedule(weekStart);
        }

    @PostMapping
    public ResponseEntity<List<PoolSession>> create(@Valid @RequestBody PoolPublishWeekScheduleRequest request) {
        List<PoolSession> sessions = poolSessionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessions);
    }


//    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("{id}")
    public ResponseEntity<List<PoolBookingResponse>> getBookingsBySession(@PathVariable("id") UUID id) {
        List<PoolBookingResponse> details = poolBookingService.getBookingDetailsForSession(id);

        List<PoolBookingResponse> sortedDetails = details.stream()
                .sorted(Comparator.comparingInt(PoolBookingResponse::getUserAge))
                .toList();
        return ResponseEntity.ok(sortedDetails);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public PoolSession update(@PathVariable UUID id, @Valid @RequestBody PoolSessionRequest request) {
        return poolSessionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        poolSessionService.delete(id);
    }


}
