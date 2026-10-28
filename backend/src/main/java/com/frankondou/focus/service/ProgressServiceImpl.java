package com.frankondou.focus.service;

import com.frankondou.focus.dto.SubjectProgressResponse;
import com.frankondou.focus.dto.WeeklyProgressResponse;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.WeeklyGoal;
import com.frankondou.focus.entity.enums.SessionStatus;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.StudySessionRepository;
import com.frankondou.focus.repository.SubjectMinutes;
import com.frankondou.focus.repository.SubjectRepository;
import com.frankondou.focus.repository.UserRepository;
import com.frankondou.focus.repository.WeeklyGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProgressServiceImpl implements ProgressService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final WeeklyGoalRepository weeklyGoalRepository;
    private final StudySessionRepository studySessionRepository;
    private final Clock clock;

    public ProgressServiceImpl(UserRepository userRepository,
                               SubjectRepository subjectRepository,
                               WeeklyGoalRepository weeklyGoalRepository,
                               StudySessionRepository studySessionRepository,
                               Clock clock) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.weeklyGoalRepository = weeklyGoalRepository;
        this.studySessionRepository = studySessionRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyProgressResponse getWeeklyProgress(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // Weeks start Monday at midnight in the user's timezone, not the server's
        ZoneId zone = ZoneId.of(user.getTimezone());
        LocalDate today = LocalDate.now(clock.withZone(zone));
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Instant weekStartInstant = weekStart.atStartOfDay(zone).toInstant();
        Instant weekEndInstant = weekStart.plusWeeks(1).atStartOfDay(zone).toInstant();

        List<Subject> subjects = subjectRepository.findCurrentSubjectsForUser(userId, today);
        if (subjects.isEmpty()) {
            return new WeeklyProgressResponse(weekStart, List.of());
        }

        List<UUID> subjectIds = subjects.stream().map(Subject::getId).toList();

        Map<UUID, Long> minutesBySubject = studySessionRepository
                .sumFocusedMinutesBySubject(subjectIds, SessionStatus.COMPLETED, weekStartInstant, weekEndInstant)
                .stream()
                .collect(Collectors.toMap(SubjectMinutes::subjectId, SubjectMinutes::minutes));

        List<SubjectProgressResponse> progress = subjects.stream()
                .map(subject -> {
                    long minutes = minutesBySubject.getOrDefault(subject.getId(), 0L);
                    BigDecimal targetHours = weeklyGoalRepository
                            .findFirstBySubject_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(subject.getId(), today)
                            .map(WeeklyGoal::getTargetHours)
                            .orElse(null);
                    return new SubjectProgressResponse(subject.getId(), subject.getName(),
                            minutesToHours(minutes), targetHours);
                })
                .toList();

        return new WeeklyProgressResponse(weekStart, progress);
    }

    private BigDecimal minutesToHours(long minutes) {
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
    }


}