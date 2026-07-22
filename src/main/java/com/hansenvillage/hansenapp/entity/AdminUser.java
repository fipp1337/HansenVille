package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
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
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();
}
