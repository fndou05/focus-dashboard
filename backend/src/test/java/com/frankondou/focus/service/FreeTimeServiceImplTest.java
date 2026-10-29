package com.frankondou.focus.service;

import com.frankondou.focus.dto.FreeTimeResponse;
import com.frankondou.focus.dto.FreeTimeWarning;
import com.frankondou.focus.dto.FreeTimeWarningCode;
import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.WeeklyGoal;
import com.frankondou.focus.entity.enums.ResponsibilityCategory;
import com.frankondou.focus.entity.enums.StrengthRating;
import com.frankondou.focus.entity.enums.SubjectCategory;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.ResponsibilityRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FreeTimeServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ResponsibilityRepository responsibilityRepository = mock(ResponsibilityRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final WeeklyGoalRepository weeklyGoalRepository = mock(WeeklyGoalRepository.class);

    // Sunday Oct 25, 2026 at 11:30 PM in New York = Monday Oct 26 at 3:30 AM UTC
    private final Clock clock = Clock.fixed(Instant.parse("2026-10-26T03:30:00Z"), ZoneOffset.UTC);
    private final LocalDate localToday = LocalDate.of(2026, 10, 25);

    private final FreeTimeServiceImpl service = new FreeTimeServiceImpl(
            userRepository, responsibilityRepository, subjectRepository, weeklyGoalRepository, clock);

    private final UUID userId = UUID.randomUUID();
    private final User user = new User("a@b.com", "Franko", "America/New_York");

    private Responsibility responsibility(ResponsibilityCategory category, String hours) {
        Responsibility responsibility = new Responsibility();
        responsibility.setCategory(category);
        responsibility.setHoursPerWeek(new BigDecimal(hours));
        return responsibility;
    }

    private Subject subject(UUID id, String classHours, String credits, SubjectCategory category, StrengthRating strength) {
        Subject subject = mock(Subject.class);
        when(subject.getId()).thenReturn(id);
        when(subject.getWeeklyClassHours()).thenReturn(new BigDecimal(classHours));
        when(subject.getCredits()).thenReturn(new BigDecimal(credits));
        when(subject.getCategory()).thenReturn(category);
        when(subject.getSelfRatedStrength()).thenReturn(strength);
        return subject;
    }

    @Test
    void getFreeTime_usesTheUsersLocalDateAndEachClassesRecommendation() {
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();
        Subject a = subject(idA, "3", "4", SubjectCategory.STEM, StrengthRating.AVERAGE);
        Subject b = subject(idB, "3.5", "3", SubjectCategory.ARTS_HUMANITIES, StrengthRating.STRONG);
        WeeklyGoal goalA = new WeeklyGoal(null, new BigDecimal("6"), LocalDate.of(2026, 9, 1));
        List<Responsibility> responsibilities = List.of(
                responsibility(ResponsibilityCategory.SLEEP, "56"),
                responsibility(ResponsibilityCategory.WORK, "10"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(responsibilityRepository.findByUser_Id(userId)).thenReturn(responsibilities);
        // The UTC date would be Oct 26, so a stub on the local date proves the user's timezone is used
        when(subjectRepository.findCurrentSubjectsForUser(userId, localToday)).thenReturn(List.of(a, b));
        when(weeklyGoalRepository.findFirstBySubject_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(idA, localToday))
                .thenReturn(Optional.of(goalA));
        when(weeklyGoalRepository.findFirstBySubject_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(idB, localToday))
                .thenReturn(Optional.empty());

        FreeTimeResponse r = service.getFreeTime(userId);

        assertEquals(new BigDecimal("6.50"), r.classHours());
        assertEquals(new BigDecimal("72.50"), r.committedHours());
        assertEquals(new BigDecimal("95.50"), r.freeHours());
        assertEquals(new BigDecimal("6.00"), r.plannedStudyHours());
        assertEquals(new BigDecimal("89.50"), r.leftoverHours());
        // STEM average 4 credits = 10.0, humanities strong 3 credits = 4.8
        assertEquals(new BigDecimal("14.80"), r.recommendedStudyHours());
        assertEquals(List.of(FreeTimeWarningCode.UNUSED_FREE_TIME),
                r.warnings().stream().map(FreeTimeWarning::code).toList());
    }

    @Test
    void getFreeTime_forUnknownUser_throwsNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getFreeTime(userId));
    }

    @Test
    void getFreeTime_withNoCurrentSubjects_hasNoClassGoalOrRecommendedHours() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(responsibilityRepository.findByUser_Id(userId)).thenReturn(List.of());
        when(subjectRepository.findCurrentSubjectsForUser(userId, localToday)).thenReturn(List.of());

        FreeTimeResponse r = service.getFreeTime(userId);

        assertEquals(new BigDecimal("0.00"), r.classHours());
        assertEquals(new BigDecimal("0.00"), r.plannedStudyHours());
        assertEquals(new BigDecimal("0.00"), r.recommendedStudyHours());
    }
}
