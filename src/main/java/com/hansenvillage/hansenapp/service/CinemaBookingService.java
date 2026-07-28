package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.dto.CinemaBookingResponse;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.repository.CinemaBookingRepository;
import com.hansenvillage.hansenapp.repository.CinemaSeatRepository;
import com.hansenvillage.hansenapp.repository.CinemaSessionRepository;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CinemaBookingService {

    private final CinemaBookingRepository cinemaBookingRepository;
    private final CinemaSessionRepository cinemaSessionRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final CinemaSeatRepository cinemaSeatRepository;

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    @Transactional
    public List<CinemaBooking> book(CinemaBookingRequest request) {
        CinemaSession session = cinemaSessionRepository.findById(request.getCinemaSessionId())
                .orElseThrow(() -> AppException.of(AppErrorCode.CINEMA_SESSION_NOT_FOUND, request.getCinemaSessionId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, request.getUserId()));

        Family family = familyRepository.findById(user.getFamilyId())
                .orElseThrow(() -> AppException.of(AppErrorCode.FAMILY_NOT_FOUND, user.getFamilyId()));

        for (UUID seatId : request.getSeatIds()) {
            cinemaSeatRepository.findById(seatId)
                    .orElseThrow(() -> AppException.of(AppErrorCode.SEAT_NOT_FOUND, seatId));

            if (cinemaBookingRepository.existsByCinemaSessionIdAndSeatId(session.getId(), seatId)) {
                throw AppException.of(AppErrorCode.SEAT_ALREADY_BOOKED, seatId);
            }
        }

        if (request.getSeatIds().size() > family.getMemberCount()) {
            throw AppException.of(AppErrorCode.TOO_MANY_SEATS);
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

        List<CinemaBooking> saved = cinemaBookingRepository.saveAll(bookings);
        log.info("Cinema booked: session={}, user={}, seats={}",
                session.getId(), request.getUserId(), request.getSeatIds().size());
        return saved;
    }

    @Transactional(readOnly = true)
    public CinemaBooking getBookingById(UUID id) {
        return cinemaBookingRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.BOOKING_NOT_FOUND, id));
    }

    @Transactional(readOnly = true)
    public List<CinemaBooking> getBookingsBySessionId(UUID sessionId) {
        return cinemaBookingRepository.getBookingsByCinemaSessionId(sessionId);
    }

    @Transactional(readOnly = true)
    public List<CinemaBooking> getAllUpcomingBookingsForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.of(AppErrorCode.USER_NOT_FOUND, userId));

        if (!SecurityUtils.isSuperAdmin() && !Objects.equals(user.getFamilyId(), SecurityUtils.currentFamilyId())) {
            throw AppException.of(AppErrorCode.INVALID_FAMILY);
        }

        return cinemaBookingRepository.findFutureByUserId(userId);
    }

    @Transactional
    public void deleteBooking(UUID id) {
        CinemaBooking booking = cinemaBookingRepository.findById(id)
                .orElseThrow(() -> AppException.of(AppErrorCode.BOOKING_NOT_FOUND, id));

        cinemaSessionRepository.findById(booking.getCinemaSessionId())
                .orElseThrow(() -> AppException.of(AppErrorCode.CINEMA_SESSION_NOT_FOUND, booking.getCinemaSessionId()));

        cinemaBookingRepository.deleteById(id);
        log.info("Cinema booking deleted: booking={}", id);
    }

    @Transactional(readOnly = true)
    public List<CinemaBookingResponse> getCinemaBookingHistory(UUID familyId) {
        List<User> members = userRepository.findByFamilyId(familyId);
        if (members.isEmpty()) {
            return List.of();
        }

        List<UUID> userIds = members.stream().map(User::getId).toList();
        List<CinemaBooking> bookings = cinemaBookingRepository.findByUserIdIn(userIds);
        if (bookings.isEmpty()) {
            return List.of();
        }

        List<UUID> sessionIds = bookings.stream().map(CinemaBooking::getCinemaSessionId).distinct().toList();
        Map<UUID, CinemaSession> sessionsById = cinemaSessionRepository.findAllById(sessionIds).stream()
                .collect(Collectors.toMap(CinemaSession::getId, Function.identity()));

        return bookings.stream()
                .sorted((left, right) -> {
                    CinemaSession leftSession = sessionsById.get(left.getCinemaSessionId());
                    CinemaSession rightSession = sessionsById.get(right.getCinemaSessionId());
                    LocalDateTime leftStart = leftSession != null ? leftSession.getStartAt() : LocalDateTime.MIN;
                    LocalDateTime rightStart = rightSession != null ? rightSession.getStartAt() : LocalDateTime.MIN;
                    return rightStart.compareTo(leftStart);
                })
                .map(booking -> new CinemaBookingResponse(
                        booking.getId(),
                        booking.getCinemaSessionId(),
                        booking.getUserId(),
                        booking.getSeatId()
                ))
                .toList();
    }
}
