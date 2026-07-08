package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateResponse;
import com.hansenvillage.hansenapp.dto.PoolWeekTemplateRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.PoolTemplate;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.PoolTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoolTemplateService {

    private final PoolTemplateRepository poolTemplateRepository;
    private final PoolSessionRepository poolSessionRepository;

    @Transactional
    public void generate(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw FamilyException.of(FamilyErrorCode.INVALID_DATES);
        }

        List<PoolTemplate> templates = poolTemplateRepository.findAll();
        if (templates.isEmpty()) {
            throw FamilyException.of(FamilyErrorCode.NO_POOL_TEMPLATES_FOUND);
        }

        startDate.datesUntil(endDate.plusDays(1)).forEach(currentDate -> {
            int dayOfWeekValue = currentDate.getDayOfWeek().getValue();

            List<PoolTemplate> matchingTemplates = templates.stream()
                    .filter(t -> t.getDayOfWeek() == dayOfWeekValue)
                    .toList();

            for (PoolTemplate template : matchingTemplates) {
                boolean exists = poolSessionRepository.existsBySessionDateAndStartTimeAndEndTime(
                        currentDate, template.getStartTime(), template.getEndTime()
                );

                if (!exists) {
                    PoolSession session = new PoolSession();
                    session.setSessionDate(currentDate);
                    session.setStartTime(template.getStartTime());
                    session.setEndTime(template.getEndTime());
                    session.setMaxCapacity(template.getMaxCapacity());
                    session.setBookedCount(0);
                    session.setStatus(SessionStatus.ACTIVE);

                    poolSessionRepository.save(session);
                }
            }
        });
    }

    @Transactional
    public void createWeeklyTemplates(PoolWeekTemplateRequest request) {
//        poolTemplateRepository.deleteAll();

        List<PoolTemplate> allTemplates = new ArrayList<>();

        for (PoolTemplateRequest dayRequest : request.getDays()) {
            List<PoolTemplate> dailyTemplates = dayRequest.getSlots().stream()
                    .map(slot -> {
                        PoolTemplate template = new PoolTemplate();
                        template.setDayOfWeek(dayRequest.getDayOfWeek());
                        template.setMaxCapacity(dayRequest.getMaxCapacity());
                        template.setStartTime(slot.getStartTime());
                        template.setEndTime(slot.getEndTime());
                        return template;
                    })
                    .toList();
            allTemplates.addAll(dailyTemplates);
        }

        poolTemplateRepository.saveAll(allTemplates);
    }

    public List<PoolTemplateResponse> getTemplates() {
        return poolTemplateRepository.findAll().stream()
                .map(template -> {
                    PoolTemplateResponse response = new PoolTemplateResponse();
                    response.setId(template.getId());
                    response.setDayOfWeek(template.getDayOfWeek());
                    response.setStartTime(template.getStartTime());
                    response.setEndTime(template.getEndTime());
                    response.setMaxCapacity(template.getMaxCapacity());
                    return response;
                })
                .collect(Collectors.toList());
    }
}