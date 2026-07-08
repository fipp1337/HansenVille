//package com.hansenvillage.hansenapp.service;
//
//import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
//import com.hansenvillage.hansenapp.entity.PoolSession;
//import com.hansenvillage.hansenapp.entity.SessionStatus;
//import com.hansenvillage.hansenapp.entity.PoolTemplate;
//import com.hansenvillage.hansenapp.exception.FamilyErrorCode;
//import com.hansenvillage.hansenapp.exception.FamilyException;
//import com.hansenvillage.hansenapp.repository.PoolSessionRepository;
//import com.hansenvillage.hansenapp.repository.PoolTemplateRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PoolTemplateServiceTest {
//
//    @Mock private PoolTemplateRepository poolTemplateRepository;
//    @Mock private PoolSessionRepository poolSessionRepository;
//
//    @Captor
//    private ArgumentCaptor<List<PoolTemplate>> listCaptor;
//    @InjectMocks
//    private PoolTemplateService poolTemplateService;
//
//
//    @Test
//    void generate_ShouldThrowException_WhenEndDateIsBeforeStartDate() {
//        LocalDate start = LocalDate.of(2026, 7, 10);
//        LocalDate end = LocalDate.of(2026, 7, 9);
//
//        FamilyException exception = assertThrows(FamilyException.class, () -> {
//            poolTemplateService.generate(start, end);
//        });
//        assertEquals(FamilyErrorCode.INVALID_DATES, exception.getErrorCode());
//    }
//
//    @Test
//    void generate_ShouldThrowException_WhenNoTemplatesExist() {
//        LocalDate start = LocalDate.of(2026, 7, 6);
//        LocalDate end = LocalDate.of(2026, 7, 7);
//
//        when(poolTemplateRepository.findAll()).thenReturn(Collections.emptyList());
//
//        FamilyException exception = assertThrows(FamilyException.class, () -> {
//            poolTemplateService.generate(start, end);
//        });
//        assertEquals(FamilyErrorCode.NO_POOL_TEMPLATES_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void generate_ShouldCreateSessionsOnlyForNonExistingSlots() {
//        LocalDate monday = LocalDate.of(2026, 7, 6);
//        LocalDate tuesday = LocalDate.of(2026, 7, 7);
//
//        PoolTemplate mondayTemplate = new PoolTemplate();
//        mondayTemplate.setDayOfWeek(1);
//        mondayTemplate.setStartTime(LocalTime.of(9, 0));
//        mondayTemplate.setEndTime(LocalTime.of(10, 0));
//        mondayTemplate.setMaxCapacity(15);
//
//        PoolTemplate tuesdayTemplate = new PoolTemplate();
//        tuesdayTemplate.setDayOfWeek(2);
//        tuesdayTemplate.setStartTime(LocalTime.of(14, 0));
//        tuesdayTemplate.setEndTime(LocalTime.of(15, 0));
//        tuesdayTemplate.setMaxCapacity(20);
//
//        when(poolTemplateRepository.findAll()).thenReturn(List.of(mondayTemplate, tuesdayTemplate));
//
//        when(poolSessionRepository.existsBySessionDateAndStartTimeAndEndTime(
//                eq(monday), eq(mondayTemplate.getStartTime()), eq(mondayTemplate.getEndTime())
//        )).thenReturn(false);
//
//        when(poolSessionRepository.existsBySessionDateAndStartTimeAndEndTime(
//                eq(tuesday), eq(tuesdayTemplate.getStartTime()), eq(tuesdayTemplate.getEndTime())
//        )).thenReturn(true);
//        poolTemplateService.generate(monday, tuesday);
//
//        ArgumentCaptor<PoolSession> sessionCaptor = ArgumentCaptor.forClass(PoolSession.class);
//        verify(poolSessionRepository, times(1)).save(sessionCaptor.capture());
//
//        PoolSession savedSession = sessionCaptor.getValue();
//        assertEquals(monday, savedSession.getSessionDate());
//        assertEquals(LocalTime.of(9, 0), savedSession.getStartTime());
//        assertEquals(SessionStatus.ACTIVE, savedSession.getStatus());
//        assertEquals(15, savedSession.getMaxCapacity());
//        assertEquals(0, savedSession.getBookedCount());
//    }
//
//
//    @Test
//    void create_ShouldMapAndSaveAllTemplates() {
//
//        PoolTemplateRequest request = new PoolTemplateRequest();
//        request.setDayOfWeek(3);
//        request.setMaxCapacity(12);
//
//        PoolTemplateRequest.TimeSlot slot1 = new PoolTemplateRequest.TimeSlot();
//        slot1.setStartTime(LocalTime.of(10, 0));
//        slot1.setEndTime(LocalTime.of(11, 0));
//
//        PoolTemplateRequest.TimeSlot slot2 = new PoolTemplateRequest.TimeSlot();
//        slot2.setStartTime(LocalTime.of(11, 0));
//        slot2.setEndTime(LocalTime.of(12, 0));
//
//        request.setSlots(List.of(slot1, slot2));
//
//        poolTemplateService.createWeeklyTemplates(request);
//
//        verify(poolTemplateRepository, times(1)).saveAll(listCaptor.capture());
//
//        List<PoolTemplate> savedTemplates = listCaptor.getValue();
//        assertEquals(2, savedTemplates.size());
//
//        PoolTemplate t1 = savedTemplates.get(0);
//        assertEquals(3, t1.getDayOfWeek());
//        assertEquals(12, t1.getMaxCapacity());
//        assertEquals(LocalTime.of(10, 0), t1.getStartTime());
//    }
//}