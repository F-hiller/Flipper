package com.ovg.flipper.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role;

    @Column(nullable = true)
    private String registrationId;

    protected User() {

    }

    @Builder
    public User(String username, String password, String email, String role, String registrationId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.registrationId = registrationId;
    }
}