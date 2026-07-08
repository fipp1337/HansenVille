package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "admin_users")
@Data
public class AdminUser {
    @Id
    @GeneratedValue
    private UUID id;
    private String email;
    private String password;

}
