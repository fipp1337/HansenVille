package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.PoolTemplateRequest;
import com.hansenvillage.hansenapp.dto.PoolTemplateResponse;
import com.hansenvillage.hansenapp.entity.PoolTemplate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PoolTemplateMapper {

    PoolTemplate toEntity(PoolTemplateRequest request);

    PoolTemplateResponse toResponse(PoolTemplate template);
}
