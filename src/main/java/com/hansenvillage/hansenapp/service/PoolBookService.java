package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.repository.PoolBookRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PoolBookService {
    private final PoolSessionRepository poolSessionRepository;
    private final PoolBookRepository poolBookRepository;
    private final UserRepository userRepository;

    @Transactional
    public PoolBooking poolBooking(PoolBookingRequest request) {
        PoolSession session = poolSessionRepository.findById(request.getPoolSessionId())
                .orElseThrow(() -> new IllegalArgumentException("SessionNotFound"));

        if (session.getBookedCount() >= session.getMaxCapacity()) {
            throw new IllegalStateException("Pool Session is full");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("UserNotFound"));

        if (poolBookRepository.existsByUserAndPoolSession(user, session)) {
            throw new IllegalStateException("Pool Session has already been Booked");
        }

        session.setBookedCount(session.getBookedCount() + 1);
        poolSessionRepository.save(session);

        PoolBooking poolBooking = new PoolBooking();
        poolBooking.setUserId(user.getId());
        poolBooking.setPoolSessionId(session.getId());

        return poolBookRepository.save(poolBooking);
    }
}