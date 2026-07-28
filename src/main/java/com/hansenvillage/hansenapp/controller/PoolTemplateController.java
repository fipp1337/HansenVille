package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolGenerateScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateResponse;
import com.hansenvillage.hansenapp.dto.PoolWeekTemplateRequest;
import com.hansenvillage.hansenapp.service.PoolTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pool/template")
@RequiredArgsConstructor
public class PoolTemplateController {

    private final PoolTemplateService poolTemplateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void createTemplates(@Valid @RequestBody PoolWeekTemplateRequest request) {
        poolTemplateService.createWeeklyTemplates(request);
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public List<PoolTemplateResponse> getTemplates() {
        return poolTemplateService.getTemplates();
    }

    @PostMapping("/generate")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public void generateSchedule(@Valid @RequestBody PoolGenerateScheduleRequest request) {
        poolTemplateService.generate(request.getStartDate(), request.getEndDate());
    }
}
