package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.CinemaBookingRequest;
import com.hansenvillage.hansenapp.entity.CinemaBooking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CinemaBookingMapper {

    CinemaBooking toEntity(CinemaBookingRequest request);
}
