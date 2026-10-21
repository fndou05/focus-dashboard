package com.frankondou.focus.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "weekly_goals")
public class WeeklyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private BigDecimal targetHours;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    // Constructors
    public WeeklyGoal() {}

    public WeeklyGoal(Subject subject, BigDecimal targetHours, LocalDate effectiveFrom) {
        this.subject = subject;
        this.targetHours = targetHours;
        this.effectiveFrom = effectiveFrom;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public Subject getSubject() { return subject; }
    public BigDecimal getTargetHours() { return targetHours; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }

    public void setSubject(Subject subject) { this.subject = subject; }
    public void setTargetHours(BigDecimal targetHours) { this.targetHours = targetHours; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
}
