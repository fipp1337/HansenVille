package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.service.PoolBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pool/booking")
@RequiredArgsConstructor
public class PoolBookingController {

    private final PoolBookingService poolBookingService;
    private final PoolBookingMapper poolBookingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PoolBookingResponse createBooking(@Valid @RequestBody PoolBookingRequest request) {
        PoolBooking booking = poolBookingService.book(request);
        return poolBookingMapper.toResponse(booking);
    }

    @GetMapping("/{id}")
    public PoolBookingResponse getBooking(@PathVariable UUID id) {
        PoolBooking booking = poolBookingService.getBookingById(id);
        return poolBookingMapper.toResponse(booking);
    }

    @GetMapping("/family/{familyId}")
    public List<PoolBookingResponse> getBookingsByFamily(@PathVariable UUID familyId) {
        List<PoolBooking> bookings = poolBookingService.getBookingsByFamilyId(familyId);
        return poolBookingMapper.toResponseList(bookings);
    }

    @GetMapping("/user/{userId}")
    public List<PoolBookingResponse> getBookingsByUser(@PathVariable UUID userId) {
        List<PoolBooking> bookings = poolBookingService.getBookingsByUserId(userId);
        return poolBookingMapper.toResponseList(bookings);
    }

    @GetMapping
    public List<PoolBookingResponse> getAllBookings() {
        List<PoolBooking> bookings = poolBookingService.getAllBookings();
        return poolBookingMapper.toResponseList(bookings);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable UUID id) {
        poolBookingService.deleteBooking(id);
    }
}