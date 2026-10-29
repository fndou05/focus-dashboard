package com.frankondou.focus.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
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

    // Snapshot of everything before the app, entered once at signup
    @Column(nullable = false)
    private BigDecimal priorCredits = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal priorGpa = BigDecimal.ZERO;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Semester> semesters;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeCutoff> gradeCutoffs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Responsibility> responsibilities;

    public User() {}

    public User(String email, String displayName, String timezone) {
        this.email = email;
        this.displayName = displayName;
        this.timezone = timezone;
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getTimezone() { return timezone; }
    public BigDecimal getPriorCredits() { return priorCredits; }
    public BigDecimal getPriorGpa() { return priorGpa; }
    public List<Semester> getSemesters() { return semesters; }
    public List<GradeCutoff> getGradeCutoffs() { return gradeCutoffs; }
    public List<Responsibility> getResponsibilities() { return responsibilities; }

    // Setters (no setId or setEmail: neither can change)
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public void setPriorCredits(BigDecimal priorCredits) { this.priorCredits = priorCredits; }
    public void setPriorGpa(BigDecimal priorGpa) { this.priorGpa = priorGpa; }
    public void setSemesters(List<Semester> semesters) { this.semesters = semesters; }
}