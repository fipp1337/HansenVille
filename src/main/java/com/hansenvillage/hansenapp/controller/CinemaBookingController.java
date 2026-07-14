package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.dto.CinemaBookingResponse;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import com.hansenvillage.hansenapp.mapper.CinemaBookingMapper;
import com.hansenvillage.hansenapp.service.CinemaBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cinema/booking")
@RequiredArgsConstructor
public class CinemaBookingController {

    private final CinemaBookingService cinemaBookingService;
    private final CinemaBookingMapper cinemaBookingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CinemaBookingResponse> createBooking(@Valid @RequestBody CinemaBookingRequest request) {
        List<CinemaBooking> bookings = cinemaBookingService.book(request);
        return cinemaBookingMapper.toResponse(bookings);
    }

    @GetMapping("/{id}")
    public CinemaBookingResponse getBookings(@PathVariable UUID id) {
        CinemaBooking booking = cinemaBookingService.getBookingById(id);
        return cinemaBookingMapper.toResponse(booking);
    }

    @GetMapping("/family/{familyId}")
    public List<CinemaBookingResponse> getBookingsByFamily(@PathVariable UUID familyId) {
        List<CinemaBooking> bookings = cinemaBookingService.getAllUpcomingBookingsForFamily(familyId);
        return cinemaBookingMapper.toResponseList(bookings);
    }

    @GetMapping("/user/{userId}")
    public List<CinemaBookingResponse> getBookingsByUser(@PathVariable UUID userId) {
        List<CinemaBooking> bookings = cinemaBookingService.getAllUpcomingBookingsForUser(userId);
        return cinemaBookingMapper.toResponseList(bookings);
    }

    @GetMapping("/session/{sessionId}")
    public List<CinemaBookingResponse> getBookingsBySessionId(@PathVariable UUID sessionId) {

        List<CinemaBooking> bookings = cinemaBookingService.getBookingsBySessionId(sessionId);
        return cinemaBookingMapper.toResponseList(bookings);
    }

//    @GetMapping
//    public List<CinemaBookingResponse> getAllBookings() {
//        List<CinemaBooking> bookings = cinemaBookingService.getAllBookings();
//        return cinemaBookingMapper.toResponseList(bookings);
//    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable UUID id) {
        cinemaBookingService.deleteBooking(id);
    }
}