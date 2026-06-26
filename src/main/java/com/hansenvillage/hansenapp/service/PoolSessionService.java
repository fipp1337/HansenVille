package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
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
import java.util.UUID;
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

    public PoolSession findById(UUID id) {

        return poolSessionRepository.findById(id)
                .orElseThrow(() -> FamilyException.of(FamilyErrorCode.SESSION_NOT_FOUND, id));
    }

        public PoolSession update(UUID id, PoolSessionRequest request) {

            PoolSession session = poolSessionRepository.findById(id)
                    .orElseThrow(() -> FamilyException.of(FamilyErrorCode.SESSION_NOT_FOUND, id));

            poolSessionMapper.updateEntity(request, session);

            return poolSessionRepository.save(session);
        }

    @Transactional
    public void delete(UUID id) {
        if (!poolSessionRepository.existsById(id)) {
            throw FamilyException.of(FamilyErrorCode.SESSION_NOT_FOUND, id);
        }
        poolSessionRepository.deleteById(id);
    }

}
