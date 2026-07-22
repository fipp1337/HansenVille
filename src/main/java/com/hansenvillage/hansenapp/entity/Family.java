package com.hansenvillage.hansenapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private String phoneNumber;

    @JsonIgnore
    private String password;

    private String address;

    private int memberCount;
}