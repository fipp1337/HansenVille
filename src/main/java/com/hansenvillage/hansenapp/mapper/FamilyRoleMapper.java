package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.entity.FamilyRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface FamilyRoleMapper {

    @Mapping(target = "familyId", source = "familyId")
    @Mapping(target = "role", constant = "USER")
    FamilyRole createUserRole(UUID familyId);
}