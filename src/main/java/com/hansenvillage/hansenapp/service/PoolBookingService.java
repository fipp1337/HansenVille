package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PoolBookingService {
    private final PoolSessionRepository poolSessionRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PoolBookingMapper poolBookingMapper;

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    @Transactional
    public PoolBookingResponse book(PoolBookingRequest request) {
        PoolSession session = poolSessionRepository.findById(request.getPoolSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, request.getPoolSessionId()));

        LocalDateTime sessionStart = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        if (LocalDateTime.now().isAfter(sessionStart)) {
            throw FamilyException.of(FamilyErrorCode.SESSION_ALREADY_STARTED, request.getPoolSessionId());
        }

        if (session.getBookedCount() >= session.getMaxCapacity()) {
            throw FamilyException.of(FamilyErrorCode.POOL_SESSION_IS_FULL, request.getPoolSessionId());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, request.getUserId()));

        UUID familyId = SecurityUtils.currentFamilyId();
        if (!Objects.equals(user.getFamilyId(), familyId)) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY, request.getUserId());
        }

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.FAMILY_NOT_FOUND, familyId));
        long memberCount = family.getMemberCount();
        long maxAllowedTickets = memberCount * 2;

        LocalDate sessionDate = session.getSessionDate();
        LocalDate monday = sessionDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate sunday = sessionDate.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        long usedTickets = poolBookingRepository.countBookingsForFamilyInWeek(familyId, monday, sunday);

        if (usedTickets >= maxAllowedTickets) {
            throw FamilyException.of(FamilyErrorCode.OUT_OF_TICKETS);
        }

        Optional<PoolBooking> existingBookingOpt = poolBookingRepository.findByUserIdAndPoolSessionId(user.getId(), session.getId());

        PoolBooking booking;

        if (existingBookingOpt.isPresent()) {
            booking = existingBookingOpt.get();

            if (booking.getStatus() == PoolBookingStatus.REGISTERED) {
                throw FamilyException.of(FamilyErrorCode.POOL_HAS_BEEN_BOOKED, request.getPoolSessionId());
            }
            booking.setStatus(PoolBookingStatus.REGISTERED);
        } else {
            booking = poolBookingMapper.toEntity(request);
            booking.setStatus(PoolBookingStatus.REGISTERED);
        }

        session.setBookedCount(session.getBookedCount() + 1);
        poolSessionRepository.save(session);

        PoolBooking savedBooking = poolBookingRepository.save(booking);

        return poolBookingMapper.toResponse(savedBooking, user, session);
    }

    public PoolBookingResponse getBookingById(UUID id) {
        PoolBooking booking = poolBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND, id));
        User user = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, booking.getUserId()));
        PoolSession session = poolSessionRepository.findById(booking.getPoolSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, booking.getPoolSessionId()));

        return poolBookingMapper.toResponse(booking, user, session);
    }

    public List<PoolBookingResponse> getBookingsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, userId);
        }
        return poolBookingRepository.findFutureRegisteredByUserId(userId);
    }

    public List<PoolBookingResponse> getBookingsByFamilyId(UUID familyId) {
        return poolBookingRepository.findFutureRegisteredByFamilyId(familyId);
    }

    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    @Transactional
    public void deleteBooking(UUID id) {
        PoolBooking booking = poolBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND, id));

        if (booking.getStatus() != PoolBookingStatus.REGISTERED) {
            throw FamilyException.of(FamilyErrorCode.BOOKING_ALREADY_CANCELLED, id);
        }

        if (!SecurityUtils.isAdmin()) {
            User bookingUser = userRepository.findById(booking.getUserId())
                    .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, booking.getUserId()));

            UUID currentFamilyId = SecurityUtils.currentFamilyId();
            if (!Objects.equals(bookingUser.getFamilyId(), currentFamilyId)) {
                throw FamilyException.of(FamilyErrorCode.NOT_YOUR_BOOKING, bookingUser.getFamilyId());
            }
        }

        PoolSession session = poolSessionRepository.findById(booking.getPoolSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, booking.getPoolSessionId()));

        LocalDateTime sessionStart = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(sessionStart)) {
            throw FamilyException.of(FamilyErrorCode.SESSION_ALREADY_STARTED, booking.getPoolSessionId());
        }

        long hoursToSession = ChronoUnit.HOURS.between(now, sessionStart);
        PoolBookingStatus finalStatus;

        if (hoursToSession >= 6) {
            finalStatus = PoolBookingStatus.CANCELED_WITH_RETURN;
            if (session.getBookedCount() > 0) {
                session.setBookedCount(session.getBookedCount() - 1);
                poolSessionRepository.save(session);
            }
        } else {
            finalStatus = PoolBookingStatus.CANCELED_WITHOUT_RETURN;
        }

        booking.setStatus(finalStatus);
        poolBookingRepository.save(booking);
    }

    public List<PoolBookingResponse> getBookingDetailsForSession(UUID sessionId) {
        if (!poolSessionRepository.existsById(sessionId)) {
            throw FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, sessionId);
        }

        List<PoolBookingResponse> details = poolBookingRepository.findBookingDetailsBySessionId(sessionId);

        return details.stream()
                .sorted(Comparator.comparing(
                        PoolBookingResponse::getUserAge,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }
}