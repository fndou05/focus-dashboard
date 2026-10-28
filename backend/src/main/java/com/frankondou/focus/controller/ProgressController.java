package com.frankondou.focus.controller;

import com.frankondou.focus.dto.WeeklyProgressResponse;
import com.frankondou.focus.service.ProgressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/users/{userId}/progress/weekly")
    public WeeklyProgressResponse getWeeklyProgress(@PathVariable("userId") UUID userId) {
        return progressService.getWeeklyProgress(userId);
    }
}