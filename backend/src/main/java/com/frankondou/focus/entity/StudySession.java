package com.frankondou.focus.entity;

import com.frankondou.focus.entity.enums.SessionMode;
import com.frankondou.focus.entity.enums.SessionStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "study_sessions")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionMode mode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Column
    private Integer focusedMinutes;

    @Column(nullable = false)
    private Instant startedAt;

    @Column
    private Instant endedAt;

    // Constructors
    public StudySession() {}

    public StudySession(Subject subject, SessionMode mode, Instant startedAt) {
        this.subject = subject;
        this.mode = mode;
        this.status = SessionStatus.ACTIVE;
        this.startedAt = startedAt;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public Subject getSubject() { return subject; }
    public SessionMode getMode() { return mode; }
    public SessionStatus getStatus() { return status; }
    public Integer getFocusedMinutes() { return focusedMinutes; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getEndedAt() { return  endedAt; }

    public void setSubject(Subject subject) { this.subject = subject; }
    public void setMode(SessionMode mode) { this.mode = mode; }
    public void setStatus(SessionStatus status) { this.status = status; }
    public void setFocusedMinutes(Integer focusedMinutes) { this.focusedMinutes = focusedMinutes; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public void setEndedAt(Instant endedAt) {this.endedAt = endedAt; }

}
