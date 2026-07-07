package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolBookingRequest;
import com.hansenvillage.hansenapp.dto.PoolBookingResponse;
import com.hansenvillage.hansenapp.entity.PoolBooking;
import com.hansenvillage.hansenapp.repository.UserRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PoolBookingMapper {

    @Autowired
    protected UserRepository userRepository;

    public abstract PoolBooking toEntity(PoolBookingRequest request);

    @Mapping(source = "id", target = "bookingId")
    public abstract PoolBookingResponse toResponse(PoolBooking entity);

    public abstract List<PoolBookingResponse> toResponseList(List<PoolBooking> entities);

    // Магия происходит здесь: MapStruct вызовет этот метод автоматически
    // после того, как создаст PoolBookingResponse
    @AfterMapping
    protected void enrichWithUserData(PoolBooking entity, @MappingTarget PoolBookingResponse response) {
        if (entity.getUserId() != null) {
            userRepository.findById(entity.getUserId()).ifPresent(user -> {
                response.setUserName(user.getName());
                response.setUserAge(user.getAge());
            });
        }
    }
}