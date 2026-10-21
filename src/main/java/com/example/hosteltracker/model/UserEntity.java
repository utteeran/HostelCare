
package com.example.hosteltracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    // ADMIN, STUDENT, RC, or STAFF
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean enabled = true;

    public UserEntity() {
    }

    public UserEntity(String loginId, String password, String role) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
