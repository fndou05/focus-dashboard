package com.frankondou.focus.controller;

import com.frankondou.focus.dto.GradeCutoffResponse;
import com.frankondou.focus.dto.UpdateGradeCutoffRequest;
import com.frankondou.focus.entity.GradeCutoff;
import com.frankondou.focus.service.GradeCutoffService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class GradeCutoffController {

    private final GradeCutoffService gradeCutoffService;

    public GradeCutoffController(GradeCutoffService gradeCutoffService) {
        this.gradeCutoffService = gradeCutoffService;
    }

    @GetMapping("/users/{userId}/grade-cutoffs")
    public List<GradeCutoffResponse> getGradeCutoffsByUserId(@PathVariable("userId") UUID userId) {
        return gradeCutoffService.getGradeCutoffsByUserId(userId).stream()
                .map(GradeCutoffResponse::from)
                .toList();
    }

    @PatchMapping("/grade-cutoffs/{id}")
    public GradeCutoffResponse updateGradeCutoff(@PathVariable("id") UUID id,
                                                 @RequestBody @Valid UpdateGradeCutoffRequest request) {
        GradeCutoff updates = new GradeCutoff(null, null, request.minPercent(), request.gradePoints());
        return GradeCutoffResponse.from(gradeCutoffService.updateGradeCutoff(id, updates));
    }
}
