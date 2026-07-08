package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.service.PoolBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pool/booking")
@RequiredArgsConstructor
public class PoolBookingController {

    private final PoolBookingService poolBookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PoolBookingResponse createBooking(@Valid @RequestBody PoolBookingRequest request) {
        return poolBookingService.book(request);
    }

    @GetMapping("/{id}")
    public PoolBookingResponse getBooking(@PathVariable UUID id) {
        return poolBookingService.getBookingById(id);
    }

    @GetMapping("/sessions/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'POOL_MANAGER')")
    public List<PoolBookingResponse> getBooksBySession(@PathVariable("id") UUID id) {
        return poolBookingService.getBookingDetailsForSession(id);
    }

    @GetMapping("/family/{familyId}")
    public List<PoolBookingResponse> getBookingsByFamily(@PathVariable UUID familyId) {
        return poolBookingService.getBookingsByFamilyId(familyId);
    }

    @GetMapping("/user/{userId}")
    public List<PoolBookingResponse> getBookingsByUser(@PathVariable UUID userId) {
        return poolBookingService.getBookingsByUserId(userId);
    }

//    @GetMapping
//    public List<PoolBookingResponse> getAllBookings() {
//        return poolBookingService.getAllBookings();
//    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable UUID id) {
        poolBookingService.deleteBooking(id);
    }
}