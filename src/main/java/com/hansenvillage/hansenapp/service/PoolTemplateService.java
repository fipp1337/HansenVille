package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateResponse;
import com.hansenvillage.hansenapp.dto.PoolWeekTemplateRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.PoolTemplate;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import com.hansenvillage.hansenapp.exception.AppErrorCode;
import com.hansenvillage.hansenapp.exception.AppException;
import com.hansenvillage.hansenapp.mapper.PoolTemplateMapper;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.PoolTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoolTemplateService {

    private final PoolTemplateRepository poolTemplateRepository;
    private final PoolSessionRepository poolSessionRepository;
    private final PoolTemplateMapper poolTemplateMapper;

    @Transactional
    public void generate(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw AppException.of(AppErrorCode.INVALID_DATES);
        }

        List<PoolTemplate> templates = poolTemplateRepository.findAll();
        if (templates.isEmpty()) {
            throw AppException.of(AppErrorCode.NO_POOL_TEMPLATES_FOUND);
        }

        startDate.datesUntil(endDate.plusDays(1)).forEach(currentDate -> {
            int dayOfWeek = currentDate.getDayOfWeek().getValue();

            templates.stream()
                    .filter(template -> template.getDayOfWeek() == dayOfWeek)
                    .forEach(template -> createSessionIfAbsent(currentDate, template));
        });
        log.info("Pool sessions generated: from={}, to={}", startDate, endDate);
    }

    @Transactional
    public void createWeeklyTemplates(PoolWeekTemplateRequest request) {
        List<PoolTemplate> templates = new ArrayList<>();

        for (PoolTemplateRequest dayRequest : request.getDays()) {
            dayRequest.getSlots().forEach(slot -> {
                PoolTemplate template = new PoolTemplate();
                template.setDayOfWeek(dayRequest.getDayOfWeek());
                template.setMaxCapacity(dayRequest.getMaxCapacity());
                template.setStartTime(slot.getStartTime());
                template.setEndTime(slot.getEndTime());
                templates.add(template);
            });
        }

        poolTemplateRepository.saveAll(templates);
        log.info("Pool templates created: count={}", templates.size());
    }

    @Transactional(readOnly = true)
    public List<PoolTemplateResponse> getTemplates() {
        return poolTemplateRepository.findAll().stream()
                .sorted(Comparator.comparing(PoolTemplate::getDayOfWeek)
                        .thenComparing(PoolTemplate::getStartTime))
                .map(poolTemplateMapper::toResponse)
                .toList();
    }

    private void createSessionIfAbsent(LocalDate date, PoolTemplate template) {
        boolean exists = poolSessionRepository.existsBySessionDateAndStartTime(date, template.getStartTime());
        if (exists) {
            return;
        }

        PoolSession session = new PoolSession();
        session.setSessionDate(date);
        session.setStartTime(template.getStartTime());
        session.setEndTime(template.getEndTime());
        session.setMaxCapacity(template.getMaxCapacity());
        session.setBookedCount(0);
        session.setStatus(SessionStatus.ACTIVE);
        poolSessionRepository.save(session);
    }
}
