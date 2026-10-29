package com.frankondou.focus.entity;

import com.frankondou.focus.entity.enums.GradeCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "grade_components")
public class GradeComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GradeCategory category;

    @Column(nullable = false)
    private BigDecimal weightPercent;

    @OneToMany(mappedBy = "component", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeEntry> entries;

    public GradeComponent() {}

    public GradeComponent(Subject subject, String name, GradeCategory category, BigDecimal weightPercent) {
        this.subject = subject;
        this.name = name;
        this.category = category;
        this.weightPercent = weightPercent;
    }

    public UUID getId() { return id; }
    public Subject getSubject() { return subject; }
    public String getName() { return name; }
    public GradeCategory getCategory() { return category; }
    public BigDecimal getWeightPercent() { return weightPercent; }
    public List<GradeEntry> getEntries() { return entries; }

    public void setSubject(Subject subject) { this.subject = subject; }
    public void setName(String name) { this.name = name; }
    public void setCategory(GradeCategory category) { this.category = category; }
    public void setWeightPercent(BigDecimal weightPercent) { this.weightPercent = weightPercent; }
}