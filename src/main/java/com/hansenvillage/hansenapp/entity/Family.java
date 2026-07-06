package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

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