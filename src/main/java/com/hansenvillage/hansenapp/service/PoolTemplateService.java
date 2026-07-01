package com.hansenvillage.hansenapp.service;

import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.PoolTemplate;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
import com.hansenvillage.hansenapp.exception.FamilyException;
import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
import com.hansenvillage.hansenapp.repository.PoolTemplateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PoolTemplateService {

    private final PoolTemplateRepository poolTemplateRepository;
    private final PoolSessionRepository poolSessionRepository;



    @Transactional
    public void generateSchedule(LocalDate startDate, LocalDate endDate) {
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
    public void createTemplate(PoolTemplateRequest request) {
        List<PoolTemplate> templates = request.getSlots().stream()
                .map(slot -> {
                    PoolTemplate template = new PoolTemplate();
                    template.setDayOfWeek(request.getDayOfWeek());
                    template.setMaxCapacity(request.getMaxCapacity());
                    template.setStartTime(slot.getStartTime());
                    template.setEndTime(slot.getEndTime());
                    return template;
                })
                .toList();

        poolTemplateRepository.saveAll(templates);
    }
}
