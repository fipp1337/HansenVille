package com.hansenvillage.hansenapp.controller;

import com.hansenvillage.hansenapp.dto.*;
import com.hansenvillage.hansenapp.entity.Family;
import com.hansenvillage.hansenapp.entity.User;
import com.hansenvillage.hansenapp.mapper.FamilyMapper;
import com.hansenvillage.hansenapp.mapper.UserMapper;
import com.hansenvillage.hansenapp.service.FamilyService;
import com.hansenvillage.hansenapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public FamilyUpdateResponse familyUpdate(
            @PathVariable UUID id,
            @Valid @RequestBody FamilyUpdateRequest request) {

        Family updated = familyService.updateFamilyInfo(id, request);
        return familyMapper.toUpdateResponse(updated);
    }

    @PutMapping("/members/{id}")
    public UserUpdateResponse userUpdate(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {

        User updated = userService.updateUser(id, request);
        return userMapper.toUpdateResponse(updated);
    }

    @PostMapping("/members")
    public UserUpdateResponse userAdd(@Valid @RequestBody AddMemberRequest request) {
        User created = userService.addNewMember(request);
        return userMapper.toUpdateResponse(created);
    }

    @DeleteMapping("/members/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userRemove(@PathVariable UUID id) {
        userService.removeMember(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFamily(@PathVariable UUID id) {
        familyService.deleteFamily(id);
    }

    @GetMapping("/{id}")
    public FamilyInfoResponse getFamilyInfo(@PathVariable UUID id) {
        return familyService.getFamilyInfoById(id);
    }

    @GetMapping("/tickets/pool")
    public PoolTicketsResponse getPoolTickets(
            @RequestParam(required = false) LocalDate date) {
        return familyService.getPoolTickets(date);
    }

    @PutMapping("/me")
    public Family updateMyFamily(@RequestBody @Valid FamilyUpdateRequest request) {
        return familyService.updateFamilyInfoByJwt(request);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyFamily() {
        familyService.deleteFamilyByJwt();
    }

    @GetMapping("/me")
    public FamilyInfoResponse getMyFamilyInfo() {
        return familyService.getFamilyInfoByJwt();
    }

    @GetMapping("/me/bookings/history")
    public FamilyBookingHistoryResponse getMyBookingHistory() {
        return familyService.getFamilyBookingHistoryByJwt();
    }
}
