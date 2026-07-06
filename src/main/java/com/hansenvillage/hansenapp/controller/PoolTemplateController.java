package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolGenerateScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
import com.hansenvillage.hansenapp.service.PoolTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pool/template")
@RequiredArgsConstructor
public class PoolTemplateController {

    private final PoolTemplateService poolTemplateService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createTemplate(@Valid @RequestBody PoolTemplateRequest request) {
        poolTemplateService.create(request);
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.OK)
    public void generateSchedule(@Valid @RequestBody PoolGenerateScheduleRequest request) {
        poolTemplateService.generate(request.getStartDate(), request.getEndDate());
    }
}