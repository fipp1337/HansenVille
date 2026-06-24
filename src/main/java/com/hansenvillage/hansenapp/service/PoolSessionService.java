package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoolSessionService {
    private final PoolSessionRepository poolSessionRepository;

    @Transactional
    public List<PoolSession> getAvailableSessionsForNextWeek(LocalDate fromDate) {
        LocalDate toDate = fromDate.plusDays(7);
        return new ArrayList<>();
    }
}
