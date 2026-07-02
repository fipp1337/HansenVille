package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class PoolBookingServiceTest {

    @Mock
    private PoolSessionRepository poolSessionRepository;
    @Mock
    private PoolBookingRepository poolBookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PoolBookingMapper poolBookingMapper;

    @InjectMocks
    PoolBookingService poolBookingService;

    @Test
    void poolBooking() {

    }

    @Test
    void getBookingById() {
    }

    @Test
    void getAllBookings() {
    }

    @Test
    void getBookingsByUserId() {
    }

    @Test
    void getBookingsByFamilyId() {
    }

    @Test
    void deleteBooking() {
    }

    @Test
    void getBookingDetailsForSession() {
    }
}