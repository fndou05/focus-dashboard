package com.frankondou.focus.controller;

import com.frankondou.focus.entity.WeeklyGoal;
import com.frankondou.focus.service.WeeklyGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
public class WeeklyGoalController {

    private final WeeklyGoalService weeklyGoalService;

    public WeeklyGoalController(WeeklyGoalService weeklyGoalService) {
        this.weeklyGoalService = weeklyGoalService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subjects/{subjectId}/weekly-goals")
    public WeeklyGoal createWeeklyGoal(@PathVariable("subjectId") UUID subjectId, @RequestBody WeeklyGoal weeklyGoal) {
        return weeklyGoalService.createWeeklyGoal(subjectId, weeklyGoal);
    }

    @GetMapping("/subjects/{subjectId}/weekly-goals")
    public List<WeeklyGoal> getWeeklyGoalsBySubjectId(@PathVariable("subjectId") UUID subjectId) {
        return weeklyGoalService.getWeeklyGoalsBySubjectId(subjectId);
    }

    @GetMapping("/weekly-goals/{id}")
    public WeeklyGoal getWeeklyGoalById(@PathVariable("id") UUID id) {
        return weeklyGoalService.getWeeklyGoalById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/weekly-goals/{id}")
    public void deleteWeeklyGoal(@PathVariable("id") UUID id) {
        weeklyGoalService.deleteWeeklyGoal(id);
    }
}