package com.project.KiTucXa.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Các hằng số cho Role
    public static final String STUDENT = "student";
    public static final String GUEST = "guest";
    public static final String DUTY_STAFF = "duty_staff";
    public static final String MANAGER = "manager";
    public static final String ADMINISTRATOR = "administrator";
}
