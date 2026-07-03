package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import com.hansenvillage.hansenapp.service.CinemaBookingService;
import com.hansenvillage.hansenapp.service.UserService;
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

    private final UserService userService;
    private final CinemaBookingService cinemaBookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CinemaBooking createBooking(@Valid @RequestBody CinemaBookingRequest request) {
        return cinemaBookingService.book(request);
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public CinemaBooking getBooking(@PathVariable UUID id) {
        return cinemaBookingService.getBookingById(id);
    }

    @GetMapping("/family/{familyId}")
    public List<CinemaBooking> getBookingsByFamily(@PathVariable UUID familyId) {
        if (!SecurityUtils.isAdmin() && !SecurityUtils.currentFamilyId().equals(familyId)) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }
        return cinemaBookingService.getBookingsByFamilyId(familyId);
    }

    @GetMapping("/user/{userId}")
    public List<CinemaBooking> getBookingsByUser(@PathVariable UUID userId) {
        return cinemaBookingService.getBookingsByUserId(userId);
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<CinemaBooking> getAllBookings() {
        return cinemaBookingService.getAllBookings();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteBooking (@PathVariable UUID id) {
        cinemaBookingService.deleteBooking(id);
    }
}
