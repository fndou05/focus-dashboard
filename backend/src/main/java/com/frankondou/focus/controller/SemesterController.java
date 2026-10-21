package com.frankondou.focus.controller;

import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.service.SemesterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public Semester createSemester(@PathVariable("userId") UUID userId, @RequestBody Semester semester) {
        return semesterService.createSemester(userId, semester);
    }

    @GetMapping("/users/{userId}/semesters")
    public List<Semester> getSemestersByUserId(@PathVariable("userId") UUID userId) {
        return semesterService.getSemestersByUserId(userId);
    }

    @GetMapping("/semesters/{id}")
    public Semester getSemesterById(@PathVariable("id") UUID id) {
        return semesterService.getSemesterById(id);
    }

    @PatchMapping("/semesters/{id}")
    public Semester updateSemester(@PathVariable("id") UUID id, @RequestBody Semester updates) {
        return semesterService.updateSemester(id, updates);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/semesters/{id}")
    public void deleteSemester(@PathVariable("id") UUID id) {
        semesterService.deleteSemester(id);
    }
}