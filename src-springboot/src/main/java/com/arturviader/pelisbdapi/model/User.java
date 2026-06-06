package com.arturviader.pelisbdapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="users",
        uniqueConstraints= {
                @UniqueConstraint(columnNames="email"),
                @UniqueConstraint(columnNames = "user_name")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Email
    @Column(nullable=false,unique=true)
    private String email;
    @NotBlank
    @Column(nullable=false)
    private String password;
    @NotBlank
    @Column(name="user_name",nullable=false,unique=true)
    private String userName;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;
    @Column(nullable=false)
    private boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

