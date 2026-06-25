package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.entity.Family;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FamilyMapper {

    @Mapping(target = "memberCount",
            expression = "java(request.getMembers().size())")
    Family toEntity(FamilyRegistrationRequest request);
}