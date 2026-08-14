package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.entity.SessionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CinemaSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posterImage", ignore = true)
    @Mapping(target = "hallId", ignore = true)
    @Mapping(target = "version", ignore = true)
    CinemaSession updateEntity(CinemaSessionRequest request, @MappingTarget CinemaSession session);

    CinemaSessionResponse toResponse(CinemaSession entity);

    CinemaSessionWithSeatsResponse toResponseWithSeats(CinemaSession entity);

    List<CinemaSessionResponse> toResponseList(List<CinemaSession> entities);

    default List<CinemaSession> toEntityList(CinemaPublishWeekScheduleRequest request) {
        if (request == null || request.getDays() == null) {
            return List.of();
        }

        return request.getDays().stream()
                .filter(day -> day.getSessions() != null)
                .flatMap(day -> day.getSessions().stream()
                        .map(slot -> {
                            CinemaSession session =
                                    toEntity(slot, day.getSessionDate());

                            session.setStatus(SessionStatus.ACTIVE);

                            return session;
                        }))
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movieName", source = "slot.movieName")
    @Mapping(target = "startAt", source = "slot.startAt")
    @Mapping(target = "duration", source = "slot.duration")
    @Mapping(target = "description", source = "slot.description")
    @Mapping(target = "hallId", source = "slot.hallId")
    @Mapping(target = "year", source = "slot.year")
    CinemaSession toEntity(CinemaSessionSlotRequest slot, LocalDate date);
}