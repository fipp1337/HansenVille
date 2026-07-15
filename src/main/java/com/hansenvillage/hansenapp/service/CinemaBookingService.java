package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.CinemaBookingMapper;
import com.hansenvillage.hansenapp.repository.*;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaBookingService {

    private final CinemaBookingRepository cinemaBookingRepository;
    private final CinemaSessionRepository cinemaSessionRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final CinemaBookingMapper cinemaBookingMapper;
    private final CinemaSeatRepository cinemaSeatRepository;

    @Retryable(
            retryFor = {
                    ObjectOptimisticLockingFailureException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    @Transactional
    public List<CinemaBooking> book(CinemaBookingRequest request) {

        CinemaSession session = cinemaSessionRepository.findById(request.getCinemaSessionId())
                .orElseThrow(() ->
                        FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, request.getCinemaSessionId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, request.getUserId()));

        if (!Objects.equals(user.getFamilyId(), SecurityUtils.currentFamilyId())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }

        UUID familyId = SecurityUtils.currentFamilyId();
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, familyId));

        long bookedSeats = cinemaBookingRepository.countByCinemaSessionId(session.getId());

//        if (bookedSeats + request.getSeatIds().size() > session.getMaxCapacity()) {
//            throw FamilyException.of(FamilyErrorCode.CINEMA_SESSION_IS_FULL);
//        }

        for (UUID seatId : request.getSeatIds()) {

            cinemaSeatRepository.findById(seatId)
                    .orElseThrow(() ->
                            FamilyException.of(FamilyErrorCode.SEAT_NOT_FOUND, seatId));

            if (cinemaBookingRepository.existsByCinemaSessionIdAndSeatId(session.getId(), seatId)) {
                throw FamilyException.of(FamilyErrorCode.SEAT_ALREADY_BOOKED, seatId);
            }
        }

        if (request.getSeatIds().size() > family.getMemberCount()) {
            throw FamilyException.of(FamilyErrorCode.TOO_MANY_SEATS);
        }

        List<CinemaBooking> bookings = request.getSeatIds().stream()
                .map(seatId -> {
                    CinemaBooking booking = new CinemaBooking();
                    booking.setCinemaSessionId(session.getId());
                    booking.setSeatId(seatId);
                    booking.setUserId(request.getUserId());
                    return booking;
                })
                .toList();

        return cinemaBookingRepository.saveAll(bookings);
    }

    public CinemaBooking getBookingById(UUID id) {
        return cinemaBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND, id));
    }

    public List<CinemaBooking> getBookingsBySessionId(UUID sessionId) {

        return cinemaBookingRepository.getBookingsByCinemaSessionId(sessionId);
    }

    public List<CinemaBooking> getAllUpcomingBookingsForFamily(UUID familyId) {

        return cinemaBookingRepository.findFutureByFamilyId(familyId);
    }

    public List<CinemaBooking> getAllUpcomingBookingsForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, userId));
        if (!SecurityUtils.isSuperAdmin() && !Objects.equals(user.getFamilyId(), SecurityUtils.currentFamilyId())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }

        return cinemaBookingRepository.findFutureByUserId(userId);
    }

    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    @Transactional
    public void deleteBooking(UUID id) {
        CinemaBooking booking = cinemaBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND, id));

        cinemaSessionRepository.findById(booking.getCinemaSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND, booking.getCinemaSessionId()));

        cinemaBookingRepository.deleteById(id);
    }
}
