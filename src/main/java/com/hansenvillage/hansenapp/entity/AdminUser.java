package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.*;

@Entity
@Table(name = "admin_users")
@Data
public class AdminUser {
    @Id
    @GeneratedValue
    private UUID id;
    private String email;
    private String password;
    private String name;
    @Convert(converter = RoleListConverter.class)
    @Column(name = "role")
    private List<Role> roles = new ArrayList<>();
}
