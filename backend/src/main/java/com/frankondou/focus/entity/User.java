package com.frankondou.focus.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", unique = true, nullable = false, length = 320)
    private String email;

    @Column(name = "display_name", nullable = false, length = 16)
    private String displayName;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;

    // Required by JPA
    public User() {
    }

    public User(String email, String displayName, String timezone) {
        this.email = email;
        this.displayName = displayName;
        this.timezone = timezone;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTimezone() {
        return timezone;
    }

    // No setId: the database generates it
    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}