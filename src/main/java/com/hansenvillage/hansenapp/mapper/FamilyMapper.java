package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.dto.FamilyRegistrationResponse;
import com.hansenvillage.hansenapp.entity.Family;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FamilyMapper {

//    @Mapping(target = "memberCount",
//            expression = "java(request.getMembers().size())")
    Family toEntity(FamilyRegistrationRequest request);

    FamilyRegistrationResponse toResponse(Family family);

}