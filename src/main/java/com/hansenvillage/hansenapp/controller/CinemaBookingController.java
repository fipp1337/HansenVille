package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.dto.CinemaBookingResponse;
import com.hansenvillage.hansenapp.mapper.CinemaBookingMapper;
import com.hansenvillage.hansenapp.service.CinemaBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cinema/bookings")
@RequiredArgsConstructor
public class CinemaBookingController {

    private final CinemaBookingService cinemaBookingService;
    private final CinemaBookingMapper cinemaBookingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CinemaBookingResponse> createBooking(@Valid @RequestBody CinemaBookingRequest request) {
        return cinemaBookingMapper.toResponse(cinemaBookingService.book(request));
    }

    @GetMapping("/{id}")
    public CinemaBookingResponse getBooking(@PathVariable UUID id) {
        return cinemaBookingMapper.toResponse(cinemaBookingService.getBookingById(id));
    }

    @GetMapping("/users/{userId}")
    public List<CinemaBookingResponse> getBookingsByUser(@PathVariable UUID userId) {
        return cinemaBookingMapper.toResponseList(cinemaBookingService.getAllUpcomingBookingsForUser(userId));
    }

    @GetMapping("/sessions/{sessionId}")
    //    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CINEMA_MANAGER')")
    public List<CinemaBookingResponse> getBookingsBySessionId(@PathVariable UUID sessionId) {
        return cinemaBookingMapper.toResponseList(cinemaBookingService.getBookingsBySessionId(sessionId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable UUID id) {
        cinemaBookingService.deleteBooking(id);
    }

    @GetMapping ("/history/families/{id}")
    public List<CinemaBookingResponse> getBookingHistory(@PathVariable UUID id) {
        return cinemaBookingService.getCinemaBookingHistory(id);
    }
}
