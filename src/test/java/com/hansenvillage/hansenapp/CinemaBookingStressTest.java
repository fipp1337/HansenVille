package com.hansenvillage.hansenapp;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.entity.*;
import com.hansenvillage.hansenapp.repository.*;
import com.hansenvillage.hansenapp.service.CinemaBookingService;
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
public class CinemaBookingStressTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CinemaBookingService cinemaBookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CinemaSessionRepository cinemaSessionRepository;
    @Autowired
    private CinemaBookingRepository cinemaBookingRepository;
    @Autowired
    private CinemaSeatRepository cinemaSeatRepository;

    @BeforeEach
    void cleanUp() {
        cinemaBookingRepository.deleteAll();
    }

    @Test
    void stressTest_ConcurrentSeatBooking() throws InterruptedException {

        List<UUID> userIds = userRepository.findAll()
                .stream()
                .map(User::getId)
                .toList();

        if (userIds.isEmpty()) {
            throw new IllegalStateException("No users in DB.");
        }

        CinemaSession session = cinemaSessionRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No cinema sessions in DB!"));

        List<CinemaSeat> seats = cinemaSeatRepository.findAll()
                .stream()
                .filter(seat -> seat.getHallId().equals(session.getHallId()))
                .toList();

        if (seats.isEmpty()) {
            throw new IllegalStateException("No seats in this hall!");
        }

        int totalRequests = Math.min(seats.size(), userIds.size());

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(totalRequests, 100));

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(totalRequests);

        UUID seatId = seats.getFirst().getId();

        for (int i = 0; i < totalRequests; i++) {
            final UUID userId = userIds.get(i);

            executor.submit(() -> {
                try {
                    start.await();

                    CinemaBookingRequest request = new CinemaBookingRequest();
                    request.setCinemaSessionId(session.getId());
                    request.setSeatIds(List.of(seatId));
                    request.setUserId(userId);

                    cinemaBookingService.book(request);

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
