package com.frankondou.focus.entity;

import com.frankondou.focus.entity.enums.StrengthRating;
import com.frankondou.focus.entity.enums.SubjectCategory;
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

    // Default of 0 for subjects created without hours
    @Column(nullable = false)
    private BigDecimal weeklyClassHours = BigDecimal.ZERO;

    // Drives the recommended study time; OTHER until the student picks one
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubjectCategory category = SubjectCategory.OTHER;

    // Empty until the semester ends; overrides the calculated grade
    @Column(length = 2)
    private String finalLetterGrade;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyGoal> weeklyGoals;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudySession> studySessions;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeComponent> gradeComponents;

    public Subject() {}

    public Subject(Semester semester, String name, BigDecimal credits, StrengthRating selfRatedStrength) {
        this.semester = semester;
        this.name = name;
        this.credits = credits;
        this.selfRatedStrength = selfRatedStrength;
    }

    // Getters
    public UUID getId() { return id; }
    public Semester getSemester() { return semester; }
    public String getName() { return name; }
    public BigDecimal getCredits() { return credits; }
    public StrengthRating getSelfRatedStrength() { return selfRatedStrength; }
    public String getFinalLetterGrade() { return finalLetterGrade; }
    public BigDecimal getWeeklyClassHours() { return weeklyClassHours; }
    public SubjectCategory getCategory() { return category; }
    public List<WeeklyGoal> getWeeklyGoals() { return weeklyGoals; }
    public List<StudySession> getStudySessions() { return studySessions; }
    public List<GradeComponent> getGradeComponents() { return gradeComponents; }

    // Setters
    public void setSemester(Semester semester) { this.semester = semester; }
    public void setName(String name) { this.name = name; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public void setSelfRatedStrength(StrengthRating selfRatedStrength) { this.selfRatedStrength = selfRatedStrength; }
    public void setFinalLetterGrade(String finalLetterGrade) { this.finalLetterGrade = finalLetterGrade; }
    public void setWeeklyClassHours(BigDecimal weeklyClassHours) { this.weeklyClassHours = weeklyClassHours; }
    public void setCategory(SubjectCategory category) { this.category = category; }
    public void setWeeklyGoals(List<WeeklyGoal> weeklyGoals) { this.weeklyGoals = weeklyGoals; }
}