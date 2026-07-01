package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PoolBookingService {
    private final PoolSessionRepository poolSessionRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PoolBookingMapper poolBookingMapper;

    @Transactional
    @Retryable(
            retryFor = {
                    ObjectOptimisticLockingFailureException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public PoolBooking poolBooking(PoolBookingRequest request) {
        PoolSession session = poolSessionRepository.findById(request.getPoolSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND));

        LocalDateTime sessionStart = LocalDateTime.of(session.getSessionDate(), session.getStartTime());

        if (LocalDateTime.now().isAfter(sessionStart)) {
            throw FamilyException.of(FamilyErrorCode.SESSION_ALREADY_STARTED);
        }

        if (session.getBookedCount() >= session.getMaxCapacity()) {
            throw FamilyException.of(FamilyErrorCode.POOL_SESSION_IS_FULL);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));

        UUID familyId = SecurityUtils.currentFamilyId();
        if (!Objects.equals(user.getFamilyId(), familyId)) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }

        if (poolBookingRepository.existsByUserIdAndPoolSessionId(user.getId(), session.getId())) {
            throw FamilyException.of(FamilyErrorCode.POOL_HAS_BEEN_BOOKED);
        }


        long memberCount = userRepository.countByFamilyId(familyId);
        long maxAllowedTickets = memberCount * 2;

        LocalDate sessionDate = session.getSessionDate();
        LocalDate monday = sessionDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate sunday = sessionDate.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));

        long usedTickets = poolBookingRepository.countBookingsForFamilyInWeek(familyId, monday, sunday);

        if (usedTickets >= maxAllowedTickets) {
            throw FamilyException.of(FamilyErrorCode.OUT_OF_TICKETS);
        }


        session.setBookedCount(session.getBookedCount() + 1);
        poolSessionRepository.save(session);

        PoolBooking poolBooking = poolBookingMapper.toEntity(request);


        return poolBookingRepository.save(poolBooking);
    }

    public Optional<PoolBooking> getBookingById(UUID id) {
        return poolBookingRepository.findById(id);
    }

    public List<PoolBooking> getAllBookings() {
        return poolBookingRepository.findAll();
    }

    public List<PoolBooking> getBookingsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));
        if (!SecurityUtils.isAdmin() && !Objects.equals(user.getFamilyId(), SecurityUtils.currentFamilyId())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }

        return poolBookingRepository.findByUserId(userId);
    }

    public List<PoolBooking> getBookingsByFamilyId(UUID familyId) {
        return poolBookingRepository.findByFamilyId(familyId);
    }

    @Transactional
    @Retryable(
            retryFor = {ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public void deleteBooking(UUID id) {
        PoolBooking booking = poolBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND));

        if (!SecurityUtils.isAdmin()) {
            User bookingUser = userRepository.findById(booking.getUserId())
                    .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));

            UUID currentFamilyId = SecurityUtils.currentFamilyId();
            if (!Objects.equals(bookingUser.getFamilyId(), currentFamilyId)) {
                throw FamilyException.of(FamilyErrorCode.NOT_YOUR_BOOKING);
            }
        }

        PoolSession session = poolSessionRepository.findById(booking.getPoolSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND));

        LocalDateTime sessionStart = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        if (ChronoUnit.HOURS.between(LocalDateTime.now(), sessionStart) < 6) {
            throw FamilyException.of(FamilyErrorCode.TIME_OUT);
        }

        if (session.getBookedCount() > 0) {
            session.setBookedCount(session.getBookedCount() - 1);
            poolSessionRepository.save(session);
        }

        poolBookingRepository.deleteById(id);
    }

    public List<PoolBookingResponse> getBookingDetailsForSession(UUID sessionId) {
        if (!poolSessionRepository.existsById(sessionId)) {
            throw FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, sessionId);
        }

        return poolBookingRepository.findBookingDetailsBySessionId(sessionId);
    }
}