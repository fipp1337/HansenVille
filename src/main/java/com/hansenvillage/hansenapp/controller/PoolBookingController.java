package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import com.hansenvillage.hansenapp.service.PoolBookingService;
import com.hansenvillage.hansenapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/pool/booking")
@RequiredArgsConstructor
public class PoolBookingController {
    private final UserService userService;
    private final PoolBookingService poolBookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public PoolBooking createBooking(@Valid @RequestBody PoolBookingRequest request) {
        return poolBookingService.book(request);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public PoolBooking getBooking(@PathVariable UUID id) {
        return poolBookingService.getBookingById(id);
    }

    @GetMapping("/family/{familyId}")
    public List<PoolBooking> getBookingsByFamily(@PathVariable UUID familyId) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.currentFamilyId().equals(familyId)) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }
        return poolBookingService.getBookingsByFamilyId(familyId);
    }

    @GetMapping("/user/{userId}")
    public List<PoolBooking> getBookingsByUser(@PathVariable UUID userId) {
        return poolBookingService.getBookingsByUserId(userId);
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<PoolBooking> getAllBookings() {
        return poolBookingService.getAllBookings();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteBooking (@PathVariable UUID id) {
        poolBookingService.deleteBooking(id);
    }
}