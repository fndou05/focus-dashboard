package com.frankondou.focus.service;

import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.enums.SessionMode;
import com.frankondou.focus.entity.enums.SessionStatus;
import com.frankondou.focus.entity.enums.StrengthRating;
import com.frankondou.focus.exception.ConflictException;
import com.frankondou.focus.repository.StudySessionRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StudySessionServiceImplTest {

    private final StudySessionRepository studySessionRepository = mock(StudySessionRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final Instant now = Instant.parse("2026-10-28T12:00:00Z");
    private final Clock clock = Clock.fixed(now, ZoneOffset.UTC);
    private final StudySessionServiceImpl service =
            new StudySessionServiceImpl(studySessionRepository, subjectRepository, clock);

    private Subject subject() {
        User user = new User("a@b.com", "Franko", "America/New_York");
        Semester semester = new Semester(user, "Fall 2026", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 20));
        return new Subject(semester, "Physics", new BigDecimal("4"), StrengthRating.AVERAGE);
    }

    @Test
    void startSession_whenUserAlreadyHasActiveSession_throwsConflict() {
        UUID subjectId = UUID.randomUUID();
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject()));
        when(studySessionRepository.existsBySubject_Semester_User_IdAndStatus(any(), eq(SessionStatus.ACTIVE)))
                .thenReturn(true);

        StudySession request = new StudySession();
        request.setMode(SessionMode.POMODORO);

        assertThrows(ConflictException.class, () -> service.startStudySession(subjectId, request));
        verify(studySessionRepository, never()).save(any());
    }

    @Test
    void endSession_whenAlreadyCompleted_throwsConflict() {
        UUID sessionId = UUID.randomUUID();
        StudySession completed = new StudySession(subject(), SessionMode.FIXED, now);
        completed.setStatus(SessionStatus.COMPLETED);
        when(studySessionRepository.findById(sessionId)).thenReturn(Optional.of(completed));

        assertThrows(ConflictException.class, () -> service.endStudySession(sessionId));
        verify(studySessionRepository, never()).save(any());
    }

    @Test
    void endSession_whenActive_marksCompletedAndRecordsMinutes() {
        UUID sessionId = UUID.randomUUID();
        StudySession active = new StudySession(subject(), SessionMode.FIXED,
                now.minus(Duration.ofMinutes(30)));
        when(studySessionRepository.findById(sessionId)).thenReturn(Optional.of(active));
        when(studySessionRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        StudySession result = service.endStudySession(sessionId);

        assertEquals(SessionStatus.COMPLETED, result.getStatus());
        assertEquals(now, result.getEndedAt());
        assertEquals(30, result.getFocusedMinutes());
    }
}