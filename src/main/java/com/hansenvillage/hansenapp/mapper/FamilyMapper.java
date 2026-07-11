package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.FamilyAdminResponse;
import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.dto.FamilyRegistrationResponse;
import com.hansenvillage.hansenapp.dto.FamilyUpdateResponse;
import com.hansenvillage.hansenapp.entity.Family;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FamilyMapper {

    //    @Mapping(target = "memberCount",
//            expression = "java(request.getMembers().size())")
    Family toEntity(FamilyRegistrationRequest request);

    FamilyRegistrationResponse toResponse(Family family);
    FamilyUpdateResponse toUpdateResponse(Family family);
    FamilyAdminResponse toAdminResponse(Family family);
    List<FamilyAdminResponse> toAdminResponse(List<Family> families);
}