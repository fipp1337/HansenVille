package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.CinemaSession;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.repository.FamilyRepository;
import com.hansenvillage.hansenapp.service.FamilyService;
import com.hansenvillage.hansenapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {
    private final FamilyMapper familyMapper;
    private final FamilyService familyService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PutMapping("/{id}")
    public ResponseEntity<FamilyUpdateResponse> familyUpdate(
            @PathVariable UUID id,
            @Valid @RequestBody FamilyUpdateRequest request) {

        Family updated = familyService.updateFamilyInfo(id, request);
        return ResponseEntity.ok(familyMapper.toUpdateResponse(updated));
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<UserUpdateResponse> userUpdate(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {

        User updated = userService.updateUser(id, request);
        return ResponseEntity.ok(userMapper.toUpdateResponse(updated));
    }

    @PostMapping("/members")
    public ResponseEntity<UserUpdateResponse> userAdd(@Valid @RequestBody AddMemberRequest request) {
        User created = userService.addNewMember(request);
        return ResponseEntity.ok(userMapper.toUpdateResponse(created));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> userRemove(@PathVariable UUID id) {
        userService.removeMember(id);
        return ResponseEntity.noContent().build();
    }
}
