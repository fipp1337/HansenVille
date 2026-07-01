package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.CinemaBookingMapper;
import com.hansenvillage.hansenapp.repository.CinemaBookingRepository;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaBookingService {

    private final CinemaBookingRepository cinemaBookingRepository;
    private final CinemaSessionRepository cinemaSessionRepository;
    private final UserRepository userRepository;
    private final CinemaBookingMapper cinemaBookingMapper;

    @Transactional
    @Retryable(
            retryFor = {
                    ObjectOptimisticLockingFailureException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public CinemaBooking cinemaBooking(CinemaBookingRequest request) {
        CinemaSession session = cinemaSessionRepository.findById(request.getCinemaSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND));

        if (session.getBookedCount() >= session.getMaxCapacity()) {
            throw FamilyException.of(FamilyErrorCode.CINEMA_SESSION_IS_FULL);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getFamilyId(), SecurityUtils.currentFamilyId())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }

        if (cinemaBookingRepository.existsByUserIdAndCinemaSessionId(user.getId(), session.getId())) {
            throw FamilyException.of(FamilyErrorCode.CINEMA_HAS_BEEN_BOOKED);
        }

        session.setBookedCount(session.getBookedCount() + 1);
        cinemaSessionRepository.save(session);

        CinemaBooking cinemaBooking = cinemaBookingMapper.toEntity(request);
        return cinemaBookingRepository.save(cinemaBooking);
    }

    public CinemaBooking getBookingById(UUID id) {
        return cinemaBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND, id));
    }

    public List<CinemaBooking> getAllBookings() {
        return cinemaBookingRepository.findAll();
    }

    public List<CinemaBooking> getBookingsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));
        if (!SecurityUtils.isAdmin() && !Objects.equals(user.getFamilyId(), SecurityUtils.currentFamilyId())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }

        return cinemaBookingRepository.findByUserId(userId);
    }

    public List<CinemaBooking> getBookingsByFamilyId(UUID familyId) {
        return cinemaBookingRepository.findByFamilyId(familyId);
    }

    @Transactional
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public void deleteBooking(UUID id) {
        CinemaBooking booking = cinemaBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND));

        CinemaSession session = cinemaSessionRepository.findById(booking.getCinemaSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.CINEMA_SESSION_NOT_FOUND));

        LocalDateTime sessionStart = LocalDateTime.of(
                session.getSessionDate(),
                session.getStartTime()
        );

        LocalDateTime now = LocalDateTime.now();

        long hoursUntilSession = ChronoUnit.HOURS.between(now, sessionStart);

        if (hoursUntilSession < 6) {
            throw FamilyException.of(FamilyErrorCode.TIME_OUT);
        }
        if (session.getBookedCount() > 0) {
            session.setBookedCount(session.getBookedCount() - 1);
            cinemaSessionRepository.save(session);
        }

        cinemaBookingRepository.deleteById(id);
    }
}
