package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.dto.CinemaBookingResponse;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CinemaBookingMapper {

    CinemaBooking toEntity(CinemaBookingRequest request);

    @Mapping(source = "id", target = "bookingId")
    CinemaBookingResponse toResponse(CinemaBooking entity);

    List<CinemaBookingResponse> toResponseList(List<CinemaBooking> entities);
}
