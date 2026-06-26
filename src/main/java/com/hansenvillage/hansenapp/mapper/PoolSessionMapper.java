package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.dto.PublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.SessionSlotsRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PoolSessionMapper {

    PoolSession toEntity(PoolSessionRequest request);

    @AfterMapping
    default void applyDefaults(@MappingTarget PoolSession entity) {
        if (entity.getMaxCapacity() == null) {
            entity.setMaxCapacity(40);
        }

        if (entity.getStatus() == null) {
            entity.setStatus(SessionStatus.ACTIVE);
        }
//        entity.setBookedCount(0);
    }

    void updateEntity(PoolSessionRequest request,
                      @MappingTarget PoolSession session);


    default List<PoolSession> toEntityList(PublishWeekScheduleRequest request) {
        if (request == null || request.getDays() == null) {
            return List.of();
        }

        return request.getDays().stream()
                .filter(day -> day.getSessions() != null)
                .flatMap(day -> day.getSessions().stream()
                        .map(slot -> toEntity(slot, day.getSessionDate())))
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessionDate", source = "date")
    @Mapping(target = "startTime", source = "slot.startTime")
    @Mapping(target = "endTime", source = "slot.endTime")
    @Mapping(target = "maxCapacity", source = "slot.maxCapacity")
    PoolSession toEntity(SessionSlotsRequest slot, LocalDate date);
}