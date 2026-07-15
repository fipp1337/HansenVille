package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.Family;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FamilyMapper {
    //    @Mapping(target = "memberCount",
//            expression = "java(request.getMembers().size())")
    Family toEntity(FamilyRegistrationRequest request);
    FamilyInfoResponse toInfoResponse(Family family);
    FamilyRegistrationResponse toResponse(Family family);
    FamilyUpdateResponse toUpdateResponse(Family family);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberCount", ignore = true)
    void updateFamilyFromRequest(FamilyUpdateRequest request, @MappingTarget Family family);
}