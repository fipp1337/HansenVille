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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoolBookingService {
    private final PoolSessionRepository poolSessionRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PoolBookingMapper poolBookingMapper;

    private List<PoolBookingResponse> filterAndMapFutureBookings(List<PoolBooking> bookings, Map<UUID, User> usersMap) {
        List<UUID> sessionIds = bookings.stream().map(PoolBooking::getPoolSessionId).distinct().toList();
        Map<UUID, PoolSession> sessionsMap = poolSessionRepository.findAllById(sessionIds).stream()
                .collect(Collectors.toMap(PoolSession::getId, Function.identity()));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return bookings.stream()
                .filter(b -> {
                    PoolSession s = sessionsMap.get(b.getPoolSessionId());
                    if (s == null) return false;
                    return s.getSessionDate().isAfter(today) ||
                            (s.getSessionDate().isEqual(today) && s.getStartTime().isAfter(now));
                })
                .map(b -> mapToResponse(b, usersMap.get(b.getUserId()), sessionsMap.get(b.getPoolSessionId())))
                .toList();
    }

    private PoolBookingResponse mapToResponse(PoolBooking booking, User user, PoolSession session) {
        return new PoolBookingResponse(
                booking.getId(),
                booking.getPoolSessionId(),
                booking.getUserId(),
                user != null ? user.getName() : "Deleted User",
                user != null ? user.getAge() : 0,
                booking.getStatus(),
                session != null ? session.getSessionDate() : null,
                session != null ? session.getStartTime() : null,
                session != null ? session.getEndTime() : null
        );
    }

    public long countBookingsForFamilyInWeek(UUID familyId, LocalDate start, LocalDate end) {
        List<UUID> userIds = userRepository.findByFamilyId(familyId).stream()
                .map(User::getId)
                .toList();
        if (userIds.isEmpty()) {
            return 0;
        }

        return poolBookingRepository.countByUserIdInAndSessionDateBetweenAndStatusIn(
                userIds,
                start,
                end,
                List.of(PoolBookingStatus.REGISTERED, PoolBookingStatus.CANCELED_WITHOUT_RETURN)
        );
    }


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

        long usedTickets = countBookingsForFamilyInWeek(familyId, monday, sunday);

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND, userId));

        List<PoolBooking> bookings = poolBookingRepository.findByUserIdAndStatus(userId, PoolBookingStatus.REGISTERED);
        if (bookings.isEmpty()) return List.of();

        return filterAndMapFutureBookings(bookings, Map.of(userId, user));
    }

    public List<PoolBookingResponse> getBookingsByFamilyId(UUID familyId) {
        List<User> members = userRepository.findByFamilyId(familyId);
        if (members.isEmpty()) return List.of();

        List<UUID> userIds = members.stream().map(User::getId).toList();
        Map<UUID, User> usersMap = members.stream().collect(Collectors.toMap(User::getId, Function.identity()));

        List<PoolBooking> bookings = poolBookingRepository.findByUserIdInAndStatus(userIds, PoolBookingStatus.REGISTERED);
        if (bookings.isEmpty()) return List.of();

        return filterAndMapFutureBookings(bookings, usersMap);
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

        if (!SecurityUtils.isSuperAdmin()) {
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

    public List<PoolBookingResponse> getBookingDetailsBySessionId(UUID sessionId) {
        List<PoolBooking> bookings = poolBookingRepository.findByPoolSessionId(sessionId);
        if (bookings.isEmpty()) return List.of();

        PoolSession session = poolSessionRepository.findById(sessionId)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND, sessionId));

        List<UUID> userIds = bookings.stream().map(PoolBooking::getUserId).distinct().toList();
        Map<UUID, User> usersMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return bookings.stream()
                .map(b -> mapToResponse(b, usersMap.get(b.getUserId()), session))
                .toList();
    }
}