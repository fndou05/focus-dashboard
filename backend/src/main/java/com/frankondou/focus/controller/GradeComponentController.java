package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CreateGradeComponentRequest;
import com.frankondou.focus.dto.GradeComponentResponse;
import com.frankondou.focus.dto.UpdateGradeComponentRequest;
import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.service.GradeComponentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class GradeComponentController {

    private final GradeComponentService gradeComponentService;

    public GradeComponentController(GradeComponentService gradeComponentService) {
        this.gradeComponentService = gradeComponentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subjects/{subjectId}/grade-components")
    public GradeComponentResponse createGradeComponent(@PathVariable("subjectId") UUID subjectId,
                                                       @RequestBody @Valid CreateGradeComponentRequest request) {
        GradeComponent component = new GradeComponent(null, request.name(), request.category(), request.weightPercent());
        return GradeComponentResponse.from(gradeComponentService.createGradeComponent(subjectId, component));
    }

    @GetMapping("/subjects/{subjectId}/grade-components")
    public List<GradeComponentResponse> getGradeComponentsBySubjectId(@PathVariable("subjectId") UUID subjectId) {
        return gradeComponentService.getGradeComponentsBySubjectId(subjectId).stream()
                .map(GradeComponentResponse::from)
                .toList();
    }

    @GetMapping("/grade-components/{id}")
    public GradeComponentResponse getGradeComponentById(@PathVariable("id") UUID id) {
        return GradeComponentResponse.from(gradeComponentService.getGradeComponentById(id));
    }

    @PatchMapping("/grade-components/{id}")
    public GradeComponentResponse updateGradeComponent(@PathVariable("id") UUID id,
                                                       @RequestBody @Valid UpdateGradeComponentRequest request) {
        GradeComponent updates = new GradeComponent(null, request.name(), request.category(), request.weightPercent());
        return GradeComponentResponse.from(gradeComponentService.updateGradeComponent(id, updates));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/grade-components/{id}")
    public void deleteGradeComponent(@PathVariable("id") UUID id) {
        gradeComponentService.deleteGradeComponent(id);
    }
}
