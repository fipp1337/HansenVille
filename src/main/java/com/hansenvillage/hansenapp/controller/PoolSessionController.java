package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.service.PoolSessionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
