package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.CinemaPublishWeekScheduleRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionRequest;
import com.hansenvillage.hansenapp.dto.CinemaSessionResponse;
import com.hansenvillage.hansenapp.dto.CinemaSessionSlotRequest;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CinemaSessionMapper {

    CinemaSession updateEntity(CinemaSessionRequest request, @MappingTarget CinemaSession session);

    CinemaSessionResponse toResponse(CinemaSession entity);

    List<CinemaSessionResponse> toResponseList(List<CinemaSession> entities);

    default List<CinemaSession> toEntityList(CinemaPublishWeekScheduleRequest request) {
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
    @Mapping(target = "movieName", source = "slot.movieName")
    @Mapping(target = "startTime", source = "slot.startTime")
    @Mapping(target = "duration", source = "slot.duration")
    @Mapping(target = "maxCapacity", source = "slot.maxCapacity")
    @Mapping(target = "sessionDate", source = "slot.sessionDate")
    CinemaSession toEntity(CinemaSessionSlotRequest slot, LocalDate date);
}