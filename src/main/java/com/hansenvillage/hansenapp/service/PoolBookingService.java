package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolBookingService {

    private final PoolSessionRepository poolSessionRepository;
    private final PoolBookingRepository poolBookingRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final PoolBookingMapper poolBookingMapper;

    @Transactional(readOnly = true)
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
                .orElseThrow(() -> AppException.of(AppErrorCode.POOL_SESSION_NOT_FOUND, request.getPoolSessionId()));

        if (session.getStatus() == SessionStatus.CANCELLED) {
            throw AppException.of(AppErrorCode.POOL_SESSION_NOT_AVAILABLE, request.getPoolSessionId());
        }

        LocalDateTime sessionStart = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        if (LocalDateTime.now().isAfter(sessionStart)) {
            throw AppException.of(AppErrorCode.SESSION_ALREADY_STARTED, request.getPoolSessionId());
        }

        if (session.getBookedCount() >= session.getMaxCapacity()) {
            throw AppException.of(AppErrorCode.POOL_SESSION_IS_FULL, request.getPoolSessionId());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, request.getUserId()));

        Family family = familyRepository.findById(user.getFamilyId())
                .orElseThrow(() -> AppException.of(AppErrorCode.FAMILY_NOT_FOUND, user.getFamilyId()));

        long memberCount = family.getMemberCount();
        long maxAllowedTickets = memberCount * 2;

        LocalDate sessionDate = session.getSessionDate();
        LocalDate monday = sessionDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = sessionDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        long usedTickets = countBookingsForFamilyInWeek(user.getFamilyId(), monday, sunday);

        if (usedTickets >= maxAllowedTickets) {
            throw AppException.of(AppErrorCode.OUT_OF_TICKETS);
        }

        Optional<PoolBooking> existingBookingOpt = poolBookingRepository.findByUserIdAndPoolSessionId(user.getId(), session.getId());

        PoolBooking booking;
        if (existingBookingOpt.isPresent()) {
            booking = existingBookingOpt.get();
            if (booking.getStatus() == PoolBookingStatus.REGISTERED) {
                throw AppException.of(AppErrorCode.POOL_HAS_BEEN_BOOKED, request.getPoolSessionId());
            }
            booking.setStatus(PoolBookingStatus.REGISTERED);
        } else {
            booking = poolBookingMapper.toEntity(request);
            booking.setStatus(PoolBookingStatus.REGISTERED);
        }

        session.setBookedCount(session.getBookedCount() + 1);
        poolSessionRepository.save(session);

        PoolBooking savedBooking = poolBookingRepository.save(booking);
        log.info("Pool booked: booking={}, session={}, user={}",
                savedBooking.getId(), session.getId(), user.getId());
        return poolBookingMapper.toResponse(savedBooking, user, session);
    }

    @Transactional
    public void deleteBooking(UUID id) {
        PoolBooking booking = poolBookingRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.BOOKING_NOT_FOUND, id));

        if (booking.getStatus() != PoolBookingStatus.REGISTERED) {
            throw AppException.of(AppErrorCode.BOOKING_ALREADY_CANCELLED, id);
        }

        if (!SecurityUtils.isSuperAdmin()) {
            User bookingUser = userRepository.findById(booking.getUserId())
                    .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, booking.getUserId()));

            UUID currentFamilyId = SecurityUtils.currentFamilyId();
            if (!Objects.equals(bookingUser.getFamilyId(), currentFamilyId)) {
                throw AppException.of(AppErrorCode.NOT_YOUR_BOOKING, bookingUser.getFamilyId());
            }
        }

        PoolSession session = poolSessionRepository.findById(booking.getPoolSessionId())
                .orElseThrow(() -> AppException.of(AppErrorCode.POOL_SESSION_NOT_FOUND, booking.getPoolSessionId()));

        LocalDateTime sessionStart = LocalDateTime.of(session.getSessionDate(), session.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(sessionStart)) {
            throw AppException.of(AppErrorCode.SESSION_ALREADY_STARTED, booking.getPoolSessionId());
        }

        if (session.getBookedCount() > 0) {
            session.setBookedCount(session.getBookedCount() - 1);
            poolSessionRepository.save(session);
        }

        long hoursToSession = ChronoUnit.HOURS.between(now, sessionStart);
        PoolBookingStatus finalStatus = (hoursToSession >= 6)
                ? PoolBookingStatus.CANCELED_WITH_RETURN
                : PoolBookingStatus.CANCELED_WITHOUT_RETURN;

        booking.setStatus(finalStatus);
        poolBookingRepository.save(booking);
        log.info("Pool booking cancelled: booking={}, status={}", id, finalStatus);
    }

    @Transactional(readOnly = true)
    public PoolBookingResponse getBookingById(UUID id) {
        PoolBooking booking = poolBookingRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.BOOKING_NOT_FOUND, id));
        User user = userRepository.findById(booking.getUserId())
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, booking.getUserId()));
        PoolSession session = poolSessionRepository.findById(booking.getPoolSessionId())
                .orElseThrow(() -> AppException.of(AppErrorCode.POOL_SESSION_NOT_FOUND, booking.getPoolSessionId()));

        return poolBookingMapper.toResponse(booking, user, session);
    }

    @Transactional(readOnly = true)
    public List<PoolBookingResponse> getBookingsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, userId));

        List<PoolBooking> bookings = poolBookingRepository.findByUserIdAndStatus(userId, PoolBookingStatus.REGISTERED);
        if (bookings.isEmpty()) return List.of();

        return filterAndMapFutureBookings(bookings, Map.of(userId, user));
    }

    @Transactional(readOnly = true)
    public List<PoolBookingResponse> getBookingsByFamilyId(UUID familyId) {
        List<User> members = userRepository.findByFamilyId(familyId);
        if (members.isEmpty()) return List.of();

        List<UUID> userIds = members.stream().map(User::getId).toList();
        Map<UUID, User> usersMap = members.stream().collect(Collectors.toMap(User::getId, Function.identity()));

        List<PoolBooking> bookings = poolBookingRepository.findByUserIdInAndStatus(userIds, PoolBookingStatus.REGISTERED);
        if (bookings.isEmpty()) return List.of();

        return filterAndMapFutureBookings(bookings, usersMap);
    }

    @Transactional(readOnly = true)
    public List<PoolBookingResponse> getBookingDetailsBySessionId(UUID sessionId) {
        List<PoolBooking> bookings = poolBookingRepository.findByPoolSessionId(sessionId);
        if (bookings.isEmpty()) return List.of();

        PoolSession session = poolSessionRepository.findById(sessionId)
                .orElseThrow(() -> AppException.of(AppErrorCode.POOL_SESSION_NOT_FOUND, sessionId));

        List<UUID> userIds = bookings.stream().map(PoolBooking::getUserId).distinct().toList();
        Map<UUID, User> usersMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return bookings.stream()
                .map(b -> poolBookingMapper.toResponse(b, usersMap.get(b.getUserId()), session))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PoolBookingResponse> getActiveBookingsByJwt() {
        return getBookingsByFamilyId(SecurityUtils.currentFamilyId());
    }

    @Transactional(readOnly = true)
    public List<PoolBookingResponse> getPoolBookingHistory(UUID familyId) {
        List<User> members = userRepository.findByFamilyId(familyId);
        if (members.isEmpty()) return List.of();

        List<UUID> userIds = members.stream().map(User::getId).toList();
        Map<UUID, User> usersMap = members.stream().collect(Collectors.toMap(User::getId, Function.identity()));

        List<PoolBooking> bookings = poolBookingRepository.findByUserIdIn(userIds);
        if (bookings.isEmpty()) return List.of();

        List<UUID> sessionIds = bookings.stream().map(PoolBooking::getPoolSessionId).distinct().toList();
        Map<UUID, PoolSession> sessionsMap = poolSessionRepository.findAllById(sessionIds).stream()
                .collect(Collectors.toMap(PoolSession::getId, Function.identity()));

        return bookings.stream()
                .map(b -> poolBookingMapper.toResponse(b, usersMap.get(b.getUserId()), sessionsMap.get(b.getPoolSessionId())))
                .sorted(Comparator.comparing(PoolBookingResponse::getSessionDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

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
                .map(b -> poolBookingMapper.toResponse(b, usersMap.get(b.getUserId()), sessionsMap.get(b.getPoolSessionId())))
                .toList();
    }
}