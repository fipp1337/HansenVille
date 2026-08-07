package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.MessageResponse;
import com.hansenvillage.hansenapp.dto.PoolGenerateScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateResponse;
import com.hansenvillage.hansenapp.dto.PoolWeekTemplateRequest;
import com.hansenvillage.hansenapp.service.PoolTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pool/templates")
@RequiredArgsConstructor
public class PoolTemplateController {

    private final PoolTemplateService poolTemplateService;

    @GetMapping
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public List<PoolTemplateResponse> getTemplates() {
        return poolTemplateService.getTemplates();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public MessageResponse createTemplates(@Valid @RequestBody PoolWeekTemplateRequest request) {
        poolTemplateService.createWeeklyTemplates(request);
        return new MessageResponse("Weekly templates created successfully");
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public MessageResponse generateSchedule(@Valid @RequestBody PoolGenerateScheduleRequest request) {
        poolTemplateService.generate(request.getStartDate(), request.getEndDate());
        return new MessageResponse("Schedule generated successfully");
    }
}