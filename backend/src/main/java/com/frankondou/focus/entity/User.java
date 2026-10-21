package com.frankondou.focus.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 320)
    private String email;

    @Column(nullable = false, length = 16)
    private String displayName;

    @Column(nullable = false, length = 64)
    private String timezone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Semester> semesters;

    // Constructors
    public User() {}

    public User(String email, String displayName, String timezone) {
        this.email = email;
        this.displayName = displayName;
        this.timezone = timezone;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getTimezone() { return timezone; }

    public List<Semester> getSemesters() { return semesters; }
    public void setSemesters(List<Semester> semesters) { this.semesters = semesters; }

    public void setEmail(String email) { this.email = email; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}