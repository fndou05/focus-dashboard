package com.frankondou.focus.entity;

import com.frankondou.focus.entity.enums.StrengthRating;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal credits;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StrengthRating selfRatedStrength;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyGoal> weeklyGoals;

    // Constructors
    public Subject() {}

    public Subject(Semester semester, String name, BigDecimal credits, StrengthRating selfRatedStrength) {
        this.semester = semester;
        this.name = name;
        this.credits = credits;
        this.selfRatedStrength = selfRatedStrength;
    }

    // Setters and getters
    public UUID getId() { return id; }
    public Semester getSemester() { return semester; }
    public String getName() { return name; }
    public BigDecimal getCredits() { return credits; }
    public StrengthRating getSelfRatedStrength() { return selfRatedStrength; }

    public List<WeeklyGoal> getWeeklyGoals() { return weeklyGoals; }
    public void setWeeklyGoals(List<WeeklyGoal> weeklyGoals) { this.weeklyGoals = weeklyGoals; }

    public void setSemester(Semester semester) { this.semester = semester; }
    public void setName(String name) { this.name = name; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public void setSelfRatedStrength(StrengthRating selfRatedStrength) { this.selfRatedStrength = selfRatedStrength; }

}
