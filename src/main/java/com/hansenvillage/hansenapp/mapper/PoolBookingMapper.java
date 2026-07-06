package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PoolBookingMapper {

    PoolBooking toEntity(PoolBookingRequest request);

    @Mapping(source = "id", target = "bookingId")
    PoolBookingResponse toResponse(PoolBooking entity);

    List<PoolBookingResponse> toResponseList(List<PoolBooking> entities);
}
