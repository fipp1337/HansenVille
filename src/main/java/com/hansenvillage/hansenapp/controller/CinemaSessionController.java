package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionResponse;
import com.hansenvillage.hansenapp.dto.CinemaSessionWithSeatsResponse;
import com.hansenvillage.hansenapp.dto.MessageResponse;
import com.hansenvillage.hansenapp.mapper.CinemaSessionMapper;
import com.hansenvillage.hansenapp.service.CinemaSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cinema/sessions")
@RequiredArgsConstructor
public class CinemaSessionController {

    private final CinemaSessionService cinemaSessionService;
    private final CinemaSessionMapper cinemaSessionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public List<CinemaSessionResponse> create(@Valid @RequestBody CinemaPublishWeekScheduleRequest request) {
        return cinemaSessionMapper.toResponseList(cinemaSessionService.create(request));
    }

    @GetMapping("/{id}")
    public CinemaSessionWithSeatsResponse getById(@PathVariable UUID id) {
        return cinemaSessionService.findById(id);
    }

    @GetMapping
    public List<CinemaSessionResponse> getWeekSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        return cinemaSessionMapper.toResponseList(cinemaSessionService.getWeekSchedule(weekStart));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public CinemaSessionResponse update(@PathVariable UUID id, @Valid @RequestBody CinemaSessionRequest request) {
        return cinemaSessionMapper.toResponse(cinemaSessionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void delete(@PathVariable UUID id) {
        cinemaSessionService.delete(id);
    }

    @PostMapping("/{id}/cancel")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public MessageResponse cancelSession(@PathVariable UUID id) {
        cinemaSessionService.cancelSession(id);
        return new MessageResponse("Cinema session cancelled successfully");
    }

    @PostMapping("/{id}/uncancel")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public MessageResponse uncancelSession(@PathVariable UUID id) {
        cinemaSessionService.uncancelSession(id);
        return new MessageResponse("Cinema session restored successfully");
    }

    @PostMapping(value = "/{id}/posters", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void uploadPoster(@PathVariable UUID id, @RequestParam MultipartFile file) {
        cinemaSessionService.uploadPoster(id, file);
    }

    @GetMapping("/{id}/posters")
    public ResponseEntity<Resource> getPoster(@PathVariable UUID id) {
        Resource resource = cinemaSessionService.getPoster(id);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @PutMapping(value = "/{id}/posters", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void updatePoster(@PathVariable UUID id, @RequestParam MultipartFile file) {
        cinemaSessionService.updatePoster(id, file);
    }

    @DeleteMapping("/{id}/posters")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public void deletePoster(@PathVariable UUID id) {
        cinemaSessionService.deletePoster(id);
    }
}