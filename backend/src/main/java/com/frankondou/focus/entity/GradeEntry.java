package com.frankondou.focus.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "grade_entries")
public class GradeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private GradeComponent component;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal scoreEarned;

    @Column(nullable = false)
    private BigDecimal scorePossible;

    @Column(nullable = false)
    private LocalDate gradedOn;

    public GradeEntry() {}

    public GradeEntry(GradeComponent component, String title, BigDecimal scoreEarned,
                      BigDecimal scorePossible, LocalDate gradedOn) {
        this.component = component;
        this.title = title;
        this.scoreEarned = scoreEarned;
        this.scorePossible = scorePossible;
        this.gradedOn = gradedOn;
    }

    public UUID getId() { return id; }
    public GradeComponent getComponent() { return component; }
    public String getTitle() { return title; }
    public BigDecimal getScoreEarned() { return scoreEarned; }
    public BigDecimal getScorePossible() { return scorePossible; }
    public LocalDate getGradedOn() { return gradedOn; }

    public void setComponent(GradeComponent component) { this.component = component; }
    public void setTitle(String title) { this.title = title; }
    public void setScoreEarned(BigDecimal scoreEarned) { this.scoreEarned = scoreEarned; }
    public void setScorePossible(BigDecimal scorePossible) { this.scorePossible = scorePossible; }
    public void setGradedOn(LocalDate gradedOn) { this.gradedOn = gradedOn; }
}