package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.AdminRegistrationRequest;
import com.hansenvillage.hansenapp.entity.AdminUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminUser toEntity(AdminRegistrationRequest request);
}