package com.frankondou.focus.service;

import com.frankondou.focus.entity.WeeklyGoal;
import java.util.List;
import java.util.UUID;

public interface WeeklyGoalService {
    WeeklyGoal createWeeklyGoal(UUID subjectId, WeeklyGoal weeklyGoal);
    List<WeeklyGoal> getWeeklyGoalsBySubjectId(UUID subjectId);
    WeeklyGoal getWeeklyGoalById(UUID id);
    void deleteWeeklyGoal(UUID id);
}
