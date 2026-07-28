package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.service.PoolBookingService;
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
    public List<PoolBookingResponse> getBookingsBySession(@PathVariable UUID id) {
        return poolBookingService.getBookingDetailsBySessionId(id);
    }

    @GetMapping("/family/{familyId}")
    public List<PoolBookingResponse> getBookingsByFamily(@PathVariable UUID familyId) {
        return poolBookingService.getBookingsByFamilyId(familyId);
    }

    @GetMapping("/user/{userId}")
    public List<PoolBookingResponse> getBookingsByUser(@PathVariable UUID userId) {
        return poolBookingService.getBookingsByUserId(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable UUID id) {
        poolBookingService.deleteBooking(id);
    }

    @GetMapping("/me/active")
    public List<PoolBookingResponse> getFamilyActivePoolBookings() {
        return poolBookingService.getActiveBookingsByJwt();
    }
}
