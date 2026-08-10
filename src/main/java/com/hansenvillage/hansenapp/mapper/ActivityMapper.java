package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.ActivityRequest;
import com.hansenvillage.hansenapp.dto.ActivityResponse;
import com.hansenvillage.hansenapp.entity.Activity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    Activity toEntity(ActivityRequest request);

    @Mapping(target = "imageUrl", expression = "java(activity.getImage() != null ? \"/api/activities/image/\" + activity.getId() : null)")
    @Mapping(target = "type", source = "type")
    ActivityResponse toResponse(Activity activity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "familyId", ignore = true)
    void updateEntity(ActivityRequest request, @MappingTarget Activity activity);
}
