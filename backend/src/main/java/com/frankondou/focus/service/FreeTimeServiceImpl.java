package com.frankondou.focus.service;

import com.frankondou.focus.dto.FreeTimeResponse;
import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.WeeklyGoal;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.ResponsibilityRepository;
import com.frankondou.focus.repository.SubjectRepository;
import com.frankondou.focus.repository.UserRepository;
import com.frankondou.focus.repository.WeeklyGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class FreeTimeServiceImpl implements FreeTimeService {

    private final UserRepository userRepository;
    private final ResponsibilityRepository responsibilityRepository;
    private final SubjectRepository subjectRepository;
    private final WeeklyGoalRepository weeklyGoalRepository;
    private final Clock clock;

    public FreeTimeServiceImpl(UserRepository userRepository,
                               ResponsibilityRepository responsibilityRepository,
                               SubjectRepository subjectRepository,
                               WeeklyGoalRepository weeklyGoalRepository,
                               Clock clock) {
        this.userRepository = userRepository;
        this.responsibilityRepository = responsibilityRepository;
        this.subjectRepository = subjectRepository;
        this.weeklyGoalRepository = weeklyGoalRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public FreeTimeResponse getFreeTime(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // "Today" is the user's local date, so a late-night Sunday doesn't count as Monday
        LocalDate today = LocalDate.now(clock.withZone(ZoneId.of(user.getTimezone())));

        List<Responsibility> responsibilities = responsibilityRepository.findByUser_Id(userId);
        List<Subject> subjects = subjectRepository.findCurrentSubjectsForUser(userId, today);

        BigDecimal classHours = BigDecimal.ZERO;
        BigDecimal plannedStudyHours = BigDecimal.ZERO;
        BigDecimal recommendedStudyHours = BigDecimal.ZERO;
        for (Subject subject : subjects) {
            BigDecimal goalHours = weeklyGoalRepository
                    .findFirstBySubject_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(subject.getId(), today)
                    .map(WeeklyGoal::getTargetHours)
                    .orElse(BigDecimal.ZERO);

            classHours = classHours.add(subject.getWeeklyClassHours());
            plannedStudyHours = plannedStudyHours.add(goalHours);
            recommendedStudyHours = recommendedStudyHours.add(FreeTimeCalculator.recommendedHoursFor(
                    subject.getCredits(), subject.getCategory(), subject.getSelfRatedStrength()));
        }

        return FreeTimeCalculator.calculate(responsibilities, classHours, plannedStudyHours, recommendedStudyHours);
    }
}
