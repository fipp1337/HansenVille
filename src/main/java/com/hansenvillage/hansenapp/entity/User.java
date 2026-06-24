package com.hansenvillage.hansenapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; /// (для обсуждения с Ильдаром) ФИО конкретного члена семьи

    @JsonIgnore // пока что так, потом сделаем DTO, это для избежания бесконечного цикла (Family содержит список Users, каждый User содержит Family, и так до бесконечности)
    private long familyId;
    //
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;


}