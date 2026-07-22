package com.hansenvillage.hansenapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupedAdminsResponse {
    private List<AdminResponse> admins;
    private List<AdminResponse> managers;
}