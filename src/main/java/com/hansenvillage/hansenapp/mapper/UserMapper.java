package com.hansenvillage.hansenapp.mapper;

import com.hansenvillage.hansenapp.dto.AddMemberRequest;
import com.hansenvillage.hansenapp.dto.FamilyRegistrationRequest;
import com.hansenvillage.hansenapp.dto.UserResponse;
import com.hansenvillage.hansenapp.dto.UserUpdateResponse;
import com.hansenvillage.hansenapp.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(AddMemberRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponse(List<User> users);

    List<User> toEntityList(List<FamilyRegistrationRequest.MemberRequest> dtoList);

    UserUpdateResponse toUpdateResponse(User user);
}