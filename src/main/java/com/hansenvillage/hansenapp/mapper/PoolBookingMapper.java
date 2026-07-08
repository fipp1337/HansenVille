package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.entity.PoolSession;
import com.hansenvillage.hansenapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PoolBookingMapper {

    PoolBooking toEntity(PoolBookingRequest request);

    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.age", target = "userAge")
    @Mapping(source = "session.sessionDate", target = "sessionDate")
    @Mapping(source = "session.startTime", target = "startTime")
    @Mapping(source = "session.endTime", target = "endTime")
    @Mapping(source = "booking.status", target = "status")
    PoolBookingResponse toResponse(PoolBooking booking, User user, PoolSession session);
}