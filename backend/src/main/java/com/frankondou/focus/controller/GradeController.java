package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CumulativeGpaResponse;
import com.frankondou.focus.dto.SemesterGpaResponse;
import com.frankondou.focus.dto.SubjectGradeResponse;
import com.frankondou.focus.service.GradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping("/subjects/{subjectId}/grade")
    public SubjectGradeResponse getSubjectGrade(@PathVariable("subjectId") UUID subjectId) {
        return gradeService.getSubjectGrade(subjectId);
    }

    @GetMapping("/semesters/{semesterId}/gpa")
    public SemesterGpaResponse getSemesterGpa(@PathVariable("semesterId") UUID semesterId) {
        return gradeService.getSemesterGpa(semesterId);
    }

    @GetMapping("/users/{userId}/gpa")
    public CumulativeGpaResponse getCumulativeGpa(@PathVariable("userId") UUID userId) {
        return gradeService.getCumulativeGpa(userId);
    }
}
