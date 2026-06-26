package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import com.hansenvillage.hansenapp.service.PoolSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.boot.origin.OriginTrackedValue.of;

@RestController
@RequestMapping("/api/session")
public class PoolSessionController {

    private final PoolSessionService poolSessionService;

    public PoolSessionController(PoolSessionService poolSessionService) {
        this.poolSessionService = poolSessionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PoolSession create(@Valid @RequestBody PoolSessionRequest request) {
        return poolSessionService.create(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PoolSession getById(@PathVariable Long id) {
        return poolSessionService.getById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.SESSION_NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PoolSession> getAll() {
        return poolSessionService.getAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PoolSession update(@PathVariable Long id, @Valid @RequestBody PoolSessionRequest request) {
        return poolSessionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {

        poolSessionService.delete(id);
    }


}
