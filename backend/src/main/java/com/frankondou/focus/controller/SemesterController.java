package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CreateSemesterRequest;
import com.frankondou.focus.dto.SemesterResponse;
import com.frankondou.focus.dto.UpdateSemesterRequest;
import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.service.SemesterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/semesters")
    public SemesterResponse createSemester(@PathVariable("userId") UUID userId,
                                           @RequestBody @Valid CreateSemesterRequest request) {
        Semester semester = new Semester();
        semester.setName(request.name());
        semester.setStartDate(request.startDate());
        semester.setEndDate(request.endDate());
        return SemesterResponse.from(semesterService.createSemester(userId, semester));
    }

    @GetMapping("/users/{userId}/semesters")
    public List<SemesterResponse> getSemestersByUserId(@PathVariable("userId") UUID userId) {
        return semesterService.getSemestersByUserId(userId).stream()
                .map(SemesterResponse::from)
                .toList();
    }

    @GetMapping("/semesters/{id}")
    public SemesterResponse getSemesterById(@PathVariable("id") UUID id) {
        return SemesterResponse.from(semesterService.getSemesterById(id));
    }

    @PatchMapping("/semesters/{id}")
    public SemesterResponse updateSemester(@PathVariable("id") UUID id,
                                           @RequestBody @Valid UpdateSemesterRequest request) {
        Semester updates = new Semester();
        updates.setName(request.name());
        updates.setStartDate(request.startDate());
        updates.setEndDate(request.endDate());
        return SemesterResponse.from(semesterService.updateSemester(id, updates));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/semesters/{id}")
    public void deleteSemester(@PathVariable("id") UUID id) {
        semesterService.deleteSemester(id);
    }
}