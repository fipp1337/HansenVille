package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.entity.CinemaSeat;
import com.hansenvillage.hansenapp.repository.CinemaSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaSeatService {

    private static final List<String> ROWS = List.of("A", "B", "C", "D");
    private static final int SEATS_PER_ROW = 9;

    private final CinemaSeatRepository cinemaSeatRepository;

    @Transactional
    public List<CinemaSeat> generateSeats(UUID hallId) {
        List<CinemaSeat> seats = new ArrayList<>();

        for (String row : ROWS) {
            for (int number = 1; number <= SEATS_PER_ROW; number++) {
                CinemaSeat seat = new CinemaSeat();
                seat.setHallId(hallId);
                seat.setSeatNumber(row + number);
                seats.add(seat);
            }
        }

        return cinemaSeatRepository.saveAll(seats);
    }
}
