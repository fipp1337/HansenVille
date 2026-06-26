package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import com.hansenvillage.hansenapp.service.PoolBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class PoolBookingController {

    private final PoolBookingService poolBookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER', 'ADMIN')")
    public PoolBooking createBooking(@Valid @RequestBody PoolBookingRequest request) {
        return poolBookingService.poolBooking(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PoolBooking getBooking(@PathVariable UUID id) {
        return poolBookingService.getBookingById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND));
    }

    @GetMapping("/family/{familyId}")
    @PreAuthorize("hasRole('USER', 'ADMIN')")
    public List<PoolBooking> getBookingsByFamily(@PathVariable UUID familyId) {
        if (!Objects.equals(SecurityUtils.currentFamilyId(), familyId)){
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }
        return poolBookingService.getBookingsByFamilyId(familyId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PoolBooking> getAllBookings() {
        return poolBookingService.getAllBookings();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER', 'ADMIN')")
    public void deleteBooking (@PathVariable UUID id) {
        poolBookingService.deleteBooking(id);
    }
}