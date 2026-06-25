package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PoolBookingMapper {

    PoolBooking toEntity(PoolBookingRequest request);
}
