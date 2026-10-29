package com.frankondou.focus.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "grade_cutoffs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "letter"}))
public class GradeCutoff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 2)
    private String letter;

    @Column(nullable = false)
    private BigDecimal minPercent;

    @Column(nullable = false)
    private BigDecimal gradePoints;

    public GradeCutoff() {}

    public GradeCutoff(User user, String letter, BigDecimal minPercent, BigDecimal gradePoints) {
        this.user = user;
        this.letter = letter;
        this.minPercent = minPercent;
        this.gradePoints = gradePoints;
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public String getLetter() { return letter; }
    public BigDecimal getMinPercent() { return minPercent; }
    public BigDecimal getGradePoints() { return gradePoints; }

    public void setUser(User user) { this.user = user; }
    public void setLetter(String letter) { this.letter = letter; }
    public void setMinPercent(BigDecimal minPercent) { this.minPercent = minPercent; }
    public void setGradePoints(BigDecimal gradePoints) { this.gradePoints = gradePoints; }
}