package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.AdminRegistrationRequest;
import com.hansenvillage.hansenapp.dto.AdminResponse;
import com.hansenvillage.hansenapp.dto.AdminUpdateRequest;
import com.hansenvillage.hansenapp.entity.AdminUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(AdminUpdateRequest request, @MappingTarget AdminUser admin);

    AdminUser toEntity(AdminRegistrationRequest request);

    AdminResponse toResponse(AdminUser user);
}