package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PoolBookingMapper {

    PoolBooking toEntity(PoolBookingRequest request);

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.age", target = "userAge")
    PoolBookingResponse toResponse(PoolBooking booking, User user);
}