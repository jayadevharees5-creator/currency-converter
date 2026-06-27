package com.example.currency_converter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a user in the system.
 * Used for HTTP Basic Authentication.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    private String username;

    // Renamed from apiKey to password since it's used for Basic Auth
    private String password;

    // Added role for future RBAC (Role-Based Access Control)
    private String role;

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
