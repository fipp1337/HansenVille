package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.PoolTemplate;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.mapper.PoolSessionMapper;
import com.hansenvillage.hansenapp.mapper.PoolTemplateMapper;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.PoolTemplateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PoolSessionService {

    private final PoolSessionRepository poolSessionRepository;
    private final PoolTemplateRepository poolTemplateRepository;
    private final PoolTemplateMapper poolTemplateMapper;
    private final PoolSessionMapper poolSessionMapper;

    @Transactional
    public List<PoolSession> getAvailableSessionsForNextWeek(LocalDate fromDate) {
        LocalDate toDate = fromDate.plusDays(7);
        return new ArrayList<>();
    }

    public PoolSession create(PoolSessionRequest request) {
        PoolSession session = poolSessionMapper.toEntity(request);
        return poolSessionRepository.save(session);
    }

    public Optional<PoolSession> getById(Long id) {
        return poolSessionRepository.findById(id);
    }

    public List<PoolSession> getAll() {
        return poolSessionRepository.findAll();
    }

    @Transactional
    public PoolSession update(Long id, PoolSessionRequest request) {
        PoolSession existing = poolSessionRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.SESSION_NOT_FOUND));

        PoolSession updated = poolSessionMapper.toEntity(request);
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setMaxCapacity(updated.getMaxCapacity());
        existing.setStatus(updated.getStatus());
        existing.setSessionDate(updated.getSessionDate());
        existing.setDayOfWeek(updated.getDayOfWeek());

        return poolSessionRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!poolSessionRepository.existsById(id)) {
            throw FamilyException.of(FamilyErrorCode.SESSION_NOT_FOUND);
        }
        poolSessionRepository.deleteById(id);
    }


}
