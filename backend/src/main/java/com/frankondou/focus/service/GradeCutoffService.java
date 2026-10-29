package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeCutoff;
import java.util.List;
import java.util.UUID;

public interface GradeCutoffService {
    List<GradeCutoff> getGradeCutoffsByUserId(UUID userId);
    GradeCutoff updateGradeCutoff(UUID id, GradeCutoff updates);
}
