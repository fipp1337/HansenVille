package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.FacilityResponse;
import com.hansenvillage.hansenapp.entity.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    @Mapping(
            target = "imageUrl",
            expression = "java(facility.getImage() != null ? \"/api/facilities/image/\" + facility.getId() : null)"
    )
    FacilityResponse toResponse(Facility facility);

    List<FacilityResponse> toResponseList(List<Facility> facilities);
}
