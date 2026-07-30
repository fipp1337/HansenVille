package com.hansenvillage.hansenapp;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import com.hansenvillage.hansenapp.service.PoolBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class PoolBookingStressTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private PoolBookingService poolBookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PoolSessionRepository poolSessionRepository;
    @Autowired
    private PoolBookingRepository poolBookingRepository;

    @BeforeEach
    void cleanUp() {
        poolBookingRepository.deleteAll();
    }

    @Test
    void stressTest() throws InterruptedException {

        List<UUID> userIds = userRepository.findAll()
                .stream()
                .map(User::getId)
                .toList();

        if (userIds.isEmpty()) {
            throw new IllegalStateException("No users in DB.");
        }

        List<PoolSession> sessions = poolSessionRepository.findAll();
        if (sessions.isEmpty()) {
            throw new IllegalStateException("No poolSessions in DB.");
        }
        UUID sessionId = sessions.getFirst().getId();

        int totalRequests = userIds.size();

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(totalRequests, 100));

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(totalRequests);

        for (UUID userId : userIds) {
            executor.submit(() -> {
                try {
                    start.await();

                    PoolBookingRequest request = new PoolBookingRequest();
                    request.setUserIds(userIds);
                    request.setPoolSessionId(sessionId);

                    poolBookingService.book(request);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    System.err.println("Booking declined: " + e.getMessage());
                } finally {
                    finish.countDown();
                }
            });
        }

        start.countDown();

        finish.await();

        executor.shutdown();
    }
}