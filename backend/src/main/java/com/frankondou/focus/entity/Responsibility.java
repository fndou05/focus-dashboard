package com.frankondou.focus.entity;

import com.frankondou.focus.entity.enums.ResponsibilityCategory;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "responsibilities")
public class Responsibility {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ResponsibilityCategory category;

    @Column(nullable = false)
    private BigDecimal hoursPerWeek;

    public Responsibility() {}

    public Responsibility(User user, String label, ResponsibilityCategory category, BigDecimal hoursPerWeek) {
        this.user = user;
        this.label = label;
        this.category = category;
        this.hoursPerWeek = hoursPerWeek;
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public String getLabel() { return label; }
    public ResponsibilityCategory getCategory() { return category; }
    public BigDecimal getHoursPerWeek() { return hoursPerWeek; }

    public void setUser(User user) { this.user = user; }
    public void setLabel(String label) { this.label = label; }
    public void setCategory(ResponsibilityCategory category) { this.category = category; }
    public void setHoursPerWeek(BigDecimal hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
}
