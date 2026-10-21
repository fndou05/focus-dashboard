package com.frankondou.focus.service;

import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.WeeklyGoal;
import com.frankondou.focus.exception.SubjectNotFoundException;
import com.frankondou.focus.exception.WeeklyGoalNotFoundException;
import com.frankondou.focus.repository.SubjectRepository;
import com.frankondou.focus.repository.WeeklyGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WeeklyGoalServiceImpl implements WeeklyGoalService {

    private final WeeklyGoalRepository weeklyGoalRepository;
    private final SubjectRepository subjectRepository;

    public WeeklyGoalServiceImpl(
            WeeklyGoalRepository weeklyGoalRepository,
            SubjectRepository subjectRepository) {
        this.weeklyGoalRepository = weeklyGoalRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public WeeklyGoal createWeeklyGoal(
            UUID subjectId, WeeklyGoal weeklyGoal) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(SubjectNotFoundException::new);

        weeklyGoal.setSubject(subject);
        return weeklyGoalRepository.save(weeklyGoal);
    }

    @Override
    public List<WeeklyGoal> getWeeklyGoalsBySubjectId(UUID subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new SubjectNotFoundException();
        }

        return weeklyGoalRepository.findBySubject_Id(subjectId);
    }

    @Override
    public WeeklyGoal getWeeklyGoalById(UUID id) {
        return weeklyGoalRepository.findById(id)
                .orElseThrow(WeeklyGoalNotFoundException::new);
    }

    @Override
    public void deleteWeeklyGoal(UUID id) {
        WeeklyGoal existing = getWeeklyGoalById(id);
        weeklyGoalRepository.delete(existing);
    }
}
