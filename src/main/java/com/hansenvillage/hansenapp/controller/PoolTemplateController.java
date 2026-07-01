package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.GenerateScheduleRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
import com.hansenvillage.hansenapp.service.PoolTemplateService;
import jakarta.validation.Valid; // Проверь импорт валидации
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/pool/template")
@RequiredArgsConstructor
public class PoolTemplateController {

    private final PoolTemplateService poolTemplateService;

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createTemplate(@Valid @RequestBody PoolTemplateRequest request) {
        poolTemplateService.createTemplate(request);
        return ResponseEntity.ok("Pool templates successfully created");
    }

    @PostMapping("/generate")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateSchedule(@Valid @RequestBody GenerateScheduleRequest request) {
        poolTemplateService.generateSchedule(request.getStartDate(), request.getEndDate());
        return ResponseEntity.ok("Schedule successfully generated from " + request.getStartDate() + " to " + request.getEndDate());
    }
}