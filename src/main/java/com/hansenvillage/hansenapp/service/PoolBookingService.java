package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new IllegalArgumentException("SessionNotFound"));

        if (session.getBookedCount() >= session.getMaxCapacity()) {
            throw new IllegalStateException("Pool Session is full");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        if (poolBookingRepository.existsByUserIdAndPoolSessionId(user.getId(), session.getId())) {
            throw new IllegalStateException("Pool Session has already been Booked");
        }

        session.setBookedCount(session.getBookedCount() + 1);
        poolSessionRepository.save(session);

        PoolBooking poolBooking = poolBookingMapper.toEntity(request);

        return poolBookingRepository.save(poolBooking);
    }
}