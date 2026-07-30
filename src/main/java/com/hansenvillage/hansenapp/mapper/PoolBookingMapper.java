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

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    PoolBooking toEntity(PoolBookingRequest request);

    @Mapping(target = "bookingId", source = "booking.id")
    @Mapping(target = "poolSessionId", source = "booking.poolSessionId")
    @Mapping(target = "userId", source = "booking.userId")
    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "userName", expression = "java(user != null ? user.getName() : \"Deleted User\")")
    @Mapping(target = "userAge", expression = "java(user != null ? user.getAge() : 0)")
    @Mapping(target = "sessionDate", source = "session.sessionDate")
    @Mapping(target = "startTime", source = "session.startTime")
    @Mapping(target = "endTime", source = "session.endTime")
    PoolBookingResponse toResponse(PoolBooking booking, User user, PoolSession session);
}