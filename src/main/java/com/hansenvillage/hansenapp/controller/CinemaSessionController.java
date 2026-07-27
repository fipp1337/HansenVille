package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionResponse;
import com.hansenvillage.hansenapp.dto.CinemaSessionWithSeatsResponse;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.mapper.CinemaSessionMapper;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import com.hansenvillage.hansenapp.service.CinemaSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/cinema/session")
@RequiredArgsConstructor
public class CinemaSessionController {

    private final CinemaSessionService cinemaSessionService;
    private final CinemaSessionMapper cinemaSessionMapper;
    private final CinemaSessionRepository cinemaSessionRepository;

    @GetMapping("/week")
    public List<CinemaSessionResponse> getWeekSchedule(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        List<CinemaSession> schedule = cinemaSessionService.getWeekSchedule(weekStart);
        return cinemaSessionMapper.toResponseList(schedule);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public List<CinemaSessionResponse> create(@Valid @RequestBody CinemaPublishWeekScheduleRequest request) {
        List<CinemaSession> sessions = cinemaSessionService.create(request);
        return cinemaSessionMapper.toResponseList(sessions);
    }

    @PostMapping(value = "/{id}/poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void uploadPoster(@PathVariable UUID id, @RequestParam MultipartFile file) {

        cinemaSessionService.uploadPoster(id, file);
    }

    @GetMapping("/{id}/poster")
    public ResponseEntity<Resource> getPoster(@PathVariable UUID id) {

        return cinemaSessionService.getPoster(id);
    }

    @PutMapping("/{id}/poster")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void updatePoster(@PathVariable UUID id, @RequestParam MultipartFile newFile) {

        cinemaSessionService.updatePoster(id, newFile);
    }

    @DeleteMapping("/{id}/poster")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//          @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void deletePoster(@PathVariable UUID id) {

        cinemaSessionService.deletePoster(id);
    }

    @GetMapping("/{id}")
    public CinemaSessionWithSeatsResponse getById(@PathVariable UUID id) {
        return cinemaSessionService.findById(id);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public CinemaSessionResponse update(@PathVariable UUID id, @Valid @RequestBody CinemaSessionRequest request) {
        CinemaSession updated = cinemaSessionService.update(id, request);
        return cinemaSessionMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void delete(@PathVariable UUID id) {
        cinemaSessionService.delete(id);
    }
}