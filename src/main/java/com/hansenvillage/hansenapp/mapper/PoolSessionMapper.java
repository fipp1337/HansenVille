package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolSessionRequest;
import com.hansenvillage.hansenapp.entity.PoolSession;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PoolSessionMapper {

    PoolSession toEntity(PoolSessionRequest request);

    @AfterMapping
    default void applyDefaults(@MappingTarget PoolSession entity) {
        if (entity.getMaxCapacity() == null) {
            entity.setMaxCapacity(40);
        }

        if (entity.getStatus() == null) {
            entity.setStatus(SessionStatus.ACTIVE);
        }

//        entity.setBookedCount(0);
    }

    void updateEntity(PoolSessionRequest request,
                      @MappingTarget PoolSession session);
}