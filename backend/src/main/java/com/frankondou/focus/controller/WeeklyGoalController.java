package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CreateWeeklyGoalRequest;
import com.frankondou.focus.dto.WeeklyGoalResponse;
import com.frankondou.focus.entity.WeeklyGoal;
import com.frankondou.focus.service.WeeklyGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    public WeeklyGoalResponse createWeeklyGoal(@PathVariable("subjectId") UUID subjectId,
                                               @RequestBody @Valid CreateWeeklyGoalRequest request) {
        WeeklyGoal goal = new WeeklyGoal();
        goal.setTargetHours(request.targetHours());
        goal.setEffectiveFrom(request.effectiveFrom());
        return WeeklyGoalResponse.from(weeklyGoalService.createWeeklyGoal(subjectId, goal));
    }

    @GetMapping("/subjects/{subjectId}/weekly-goals")
    public List<WeeklyGoalResponse> getWeeklyGoalsBySubjectId(@PathVariable("subjectId") UUID subjectId) {
        return weeklyGoalService.getWeeklyGoalsBySubjectId(subjectId).stream()
                .map(WeeklyGoalResponse::from)
                .toList();
    }

    @GetMapping("/weekly-goals/{id}")
    public WeeklyGoalResponse getWeeklyGoalById(@PathVariable("id") UUID id) {
        return WeeklyGoalResponse.from(weeklyGoalService.getWeeklyGoalById(id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/weekly-goals/{id}")
    public void deleteWeeklyGoal(@PathVariable("id") UUID id) {
        weeklyGoalService.deleteWeeklyGoal(id);
    }
}