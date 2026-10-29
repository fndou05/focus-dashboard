package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CreateGradeEntryRequest;
import com.frankondou.focus.dto.GradeEntryResponse;
import com.frankondou.focus.dto.UpdateGradeEntryRequest;
import com.frankondou.focus.entity.GradeEntry;
import com.frankondou.focus.service.GradeEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class GradeEntryController {

    private final GradeEntryService gradeEntryService;

    public GradeEntryController(GradeEntryService gradeEntryService) {
        this.gradeEntryService = gradeEntryService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/grade-components/{componentId}/grade-entries")
    public GradeEntryResponse createEntry(@PathVariable("componentId") UUID componentId,
                                          @RequestBody @Valid CreateGradeEntryRequest request) {
        GradeEntry entry = new GradeEntry();
        entry.setTitle(request.title());
        entry.setScoreEarned(request.scoreEarned());
        entry.setScorePossible(request.scorePossible());
        entry.setGradedOn(request.gradedOn());
        return GradeEntryResponse.from(gradeEntryService.createEntry(componentId, entry));
    }

    @GetMapping("/grade-components/{componentId}/grade-entries")
    public List<GradeEntryResponse> getEntriesByComponentId(@PathVariable("componentId") UUID componentId) {
        return gradeEntryService.getEntriesByComponentId(componentId).stream()
                .map(GradeEntryResponse::from)
                .toList();
    }

    @GetMapping("/grade-entries/{id}")
    public GradeEntryResponse getEntryById(@PathVariable("id") UUID id) {
        return GradeEntryResponse.from(gradeEntryService.getEntryById(id));
    }

    @PatchMapping("/grade-entries/{id}")
    public GradeEntryResponse updateEntry(@PathVariable("id") UUID id,
                                          @RequestBody @Valid UpdateGradeEntryRequest request) {
        GradeEntry updates = new GradeEntry();
        updates.setTitle(request.title());
        updates.setScoreEarned(request.scoreEarned());
        updates.setScorePossible(request.scorePossible());
        updates.setGradedOn(request.gradedOn());
        return GradeEntryResponse.from(gradeEntryService.updateEntry(id, updates));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/grade-entries/{id}")
    public void deleteEntry(@PathVariable("id") UUID id) {
        gradeEntryService.deleteEntry(id);
    }
}
