package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private final PoolBookingMapper poolBookingMapper;

    @Transactional
    public PoolBooking poolBooking(PoolBookingRequest request) {
        PoolSession session = poolSessionRepository.findById(request.getPoolSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.POOL_SESSION_NOT_FOUND));

        if (session.getBookedCount() >= session.getMaxCapacity()) {
            throw FamilyException.of(FamilyErrorCode.POOL_SESSION_IS_FULL);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getFamilyId(), SecurityUtils.currentFamilyId())) {
            throw FamilyException.of(FamilyErrorCode.INVALID_FAMILY);
        }

        if (poolBookingRepository.existsByUserIdAndPoolSessionId(user.getId(), session.getId())) {
            throw FamilyException.of(FamilyErrorCode.POOL_HAS_BEEN_BOOKED);
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
    public void deleteBooking(UUID id) {
        PoolBooking booking = poolBookingRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.BOOKING_NOT_FOUND));

        PoolSession session = poolSessionRepository.findById(booking.getPoolSessionId())
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.SESSION_NOT_FOUND));

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
            poolSessionRepository.save(session);
        }

        poolBookingRepository.deleteById(id);
    }


}