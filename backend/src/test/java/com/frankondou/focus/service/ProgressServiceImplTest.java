package com.frankondou.focus.service;

import com.frankondou.focus.dto.SubjectProgressResponse;
import com.frankondou.focus.dto.WeeklyProgressResponse;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.WeeklyGoal;
import com.frankondou.focus.entity.enums.SessionStatus;
import com.frankondou.focus.repository.StudySessionRepository;
import com.frankondou.focus.repository.SubjectMinutes;
import com.frankondou.focus.repository.SubjectRepository;
import com.frankondou.focus.repository.UserRepository;
import com.frankondou.focus.repository.WeeklyGoalRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class ProgressServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final WeeklyGoalRepository weeklyGoalRepository = mock(WeeklyGoalRepository.class);
    private final StudySessionRepository studySessionRepository = mock(StudySessionRepository.class);

    // Sunday Oct 25, 2026 at 11:30 PM in New York = Monday Oct 26 at 3:30 AM UTC
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-26T03:30:00Z"), ZoneOffset.UTC);

    private final ProgressServiceImpl service = new ProgressServiceImpl(
            userRepository, subjectRepository, weeklyGoalRepository, studySessionRepository, clock);

    @Test
    void weeklyProgress_usesUsersTimezoneForWeekBoundaries() {
        UUID userId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        LocalDate localToday = LocalDate.of(2026, 10, 25); // still Sunday in New York

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User("a@b.com", "Franko", "America/New_York")));

        Subject physics = mock(Subject.class);
        when(physics.getId()).thenReturn(subjectId);
        when(physics.getName()).thenReturn("Physics");
        when(subjectRepository.findCurrentSubjectsForUser(userId, localToday)).thenReturn(List.of(physics));

        // The week must be Monday Oct 19 00:00 to Monday Oct 26 00:00, New York time (UTC-4)
        Instant expectedStart = Instant.parse("2026-10-19T04:00:00Z");
        Instant expectedEnd = Instant.parse("2026-10-26T04:00:00Z");
        when(studySessionRepository.sumFocusedMinutesBySubject(
                List.of(subjectId), SessionStatus.COMPLETED, expectedStart, expectedEnd))
                .thenReturn(List.of(new SubjectMinutes(subjectId, 270L)));

        WeeklyGoal goal = new WeeklyGoal();
        goal.setTargetHours(new BigDecimal("7"));
        when(weeklyGoalRepository.findFirstBySubject_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                subjectId, localToday)).thenReturn(Optional.of(goal));

        WeeklyProgressResponse response = service.getWeeklyProgress(userId);

        assertEquals(LocalDate.of(2026, 10, 19), response.weekStart());
        SubjectProgressResponse physicsProgress = response.subjects().get(0);
        assertEquals(new BigDecimal("4.5"), physicsProgress.loggedHours());
        assertEquals(new BigDecimal("7"), physicsProgress.targetHours());
    }

    @Test
    void weeklyProgress_withNoCurrentSubjects_returnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User("a@b.com", "Franko", "America/New_York")));
        when(subjectRepository.findCurrentSubjectsForUser(any(), any())).thenReturn(List.of());

        WeeklyProgressResponse response = service.getWeeklyProgress(userId);

        assertTrue(response.subjects().isEmpty());
    }
}