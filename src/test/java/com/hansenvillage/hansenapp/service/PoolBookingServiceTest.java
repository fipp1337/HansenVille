package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.mapper.PoolBookingMapper;
import com.hansenvillage.hansenapp.repository.PoolBookingRepository;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void getBookingById_ShouldReturnBooking_WhenBookingExists() {
        UUID bookingId = UUID.randomUUID();
        PoolBooking expectedBooking = new PoolBooking();
        expectedBooking.setId(bookingId);

        when(poolBookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        Optional<PoolBooking> result = poolBookingService.getBookById(bookingId);

        assertTrue(result.isPresent());
        assertEquals(bookingId, result.get().getId());

        verify(poolBookingRepository, times(1)).findById(bookingId);
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