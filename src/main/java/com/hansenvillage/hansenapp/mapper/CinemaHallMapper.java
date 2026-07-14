package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.CinemaHallRequest;
import com.hansenvillage.hansenapp.dto.CinemaHallResponse;
import com.hansenvillage.hansenapp.entity.CinemaHall;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CinemaHallMapper {

    @Mapping(target = "id", ignore = true)
    CinemaHall toEntity(String name);

    @Mapping(target = "hallId", source = "id")
    CinemaHallResponse toResponse(CinemaHall entity);
}