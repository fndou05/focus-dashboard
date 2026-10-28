package com.frankondou.focus.service;

import com.frankondou.focus.dto.WeeklyProgressResponse;
import java.util.UUID;

public interface ProgressService {
    WeeklyProgressResponse getWeeklyProgress(UUID userId);
}