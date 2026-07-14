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

    private final CinemaSeatRepository cinemaSeatRepository;

    @Transactional
    public List<CinemaSeat> generateSeats(UUID hallId) {

        List<CinemaSeat> seats = new ArrayList<>();
        List<String> rows = List.of("A", "B", "C", "D");

        for (String row : rows) {
            for (int sofa = 1; sofa <= 9; sofa++) {

                CinemaSeat seat = new CinemaSeat();
                seat.setHallId(hallId);
                seat.setSofaNumber(row + sofa);

                seats.add(seat);
            }
        }

        return cinemaSeatRepository.saveAll(seats);
    }
}
