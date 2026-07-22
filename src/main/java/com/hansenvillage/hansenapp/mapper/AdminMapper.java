package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.AdminRegistrationRequest;
import com.hansenvillage.hansenapp.dto.AdminUpdateRequest;
import com.hansenvillage.hansenapp.entity.AdminUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminUser toEntity(AdminRegistrationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateAdminFromRequest(AdminUpdateRequest request, @MappingTarget AdminUser admin);
}