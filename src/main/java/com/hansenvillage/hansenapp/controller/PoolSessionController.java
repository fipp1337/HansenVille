package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import com.hansenvillage.hansenapp.service.PoolSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.boot.origin.OriginTrackedValue.of;

@RestController
@RequestMapping("/api/session")
public class PoolSessionController {

    private final PoolSessionService poolSessionService;

    public PoolSessionController(PoolSessionService poolSessionService) {
        this.poolSessionService = poolSessionService;
    }

    @PostMapping
    public ResponseEntity<List<PoolSession>> create(
            @Valid @RequestBody PublishWeekScheduleRequest request) {

        List<PoolSession> sessions = poolSessionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessions);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public PoolSession getById(@PathVariable UUID id) {
        return poolSessionService.findById(id);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public PoolSession update(@PathVariable UUID id, @Valid @RequestBody PoolSessionRequest request) {
        return poolSessionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {

        poolSessionService.delete(id);
    }


}
