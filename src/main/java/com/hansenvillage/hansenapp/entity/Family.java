package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "families")
public class Family {

    @Id
    @GeneratedValue
    private UUID id;

    @Email
    private String email;

    private String password;

    private String address;

    private int memberCount;
}