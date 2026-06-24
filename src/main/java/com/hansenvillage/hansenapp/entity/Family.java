package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "families")
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "username", nullable = false, unique = true, length = 40)
    private String username;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<User> members = new ArrayList<>();

}