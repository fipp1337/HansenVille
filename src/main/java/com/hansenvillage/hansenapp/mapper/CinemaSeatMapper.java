package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.CinemaSeatResponse;
import com.hansenvillage.hansenapp.dto.CinemaSeatWithAvailableResponse;
import com.hansenvillage.hansenapp.entity.CinemaSeat;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CinemaSeatMapper {

    CinemaSeatResponse toResponse(CinemaSeat entity);

    CinemaSeatWithAvailableResponse toResponseWithAvailable(CinemaSeat entity);

    List<CinemaSeatResponse> toResponseList(List<CinemaSeat> seats);
}
