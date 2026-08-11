package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "invite_codes")
@NoArgsConstructor
@Getter
@Setter
public class InviteCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String code;

    @Enumerated(EnumType.STRING)
    private InviteCodeStatus status = InviteCodeStatus.AVAILABLE;

    private String email;
}