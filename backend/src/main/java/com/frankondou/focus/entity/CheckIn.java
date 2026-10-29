package com.frankondou.focus.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // unique backs the rule of one check-in per session
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private StudySession session;

    // Ratings are 1-5. Mood: 1 bad, 5 good. Difficulty: 1 very easy, 5 very hard.
    // Confidence: 1 lost, 5 fully understand.
    @Column(nullable = false)
    private int moodRating;

    @Column(nullable = false)
    private int difficultyRating;

    @Column(nullable = false)
    private int confidenceRating;

    @Column(length = 1000)
    private String notes;

    public CheckIn() {}

    public CheckIn(StudySession session, int moodRating, int difficultyRating, int confidenceRating, String notes) {
        this.session = session;
        this.moodRating = moodRating;
        this.difficultyRating = difficultyRating;
        this.confidenceRating = confidenceRating;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public StudySession getSession() { return session; }
    public int getMoodRating() { return moodRating; }
    public int getDifficultyRating() { return difficultyRating; }
    public int getConfidenceRating() { return confidenceRating; }
    public String getNotes() { return notes; }

    public void setSession(StudySession session) { this.session = session; }
    public void setMoodRating(int moodRating) { this.moodRating = moodRating; }
    public void setDifficultyRating(int difficultyRating) { this.difficultyRating = difficultyRating; }
    public void setConfidenceRating(int confidenceRating) { this.confidenceRating = confidenceRating; }
    public void setNotes(String notes) { this.notes = notes; }
}
