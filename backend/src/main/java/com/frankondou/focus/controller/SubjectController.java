package com.frankondou.focus.controller;

import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/semesters/{semesterId}/subjects")
    public Subject createSubject(@PathVariable("semesterId") UUID semesterId, @RequestBody Subject subject) {
        return subjectService.createSubject(semesterId, subject);
    }

    @GetMapping("/semesters/{semesterId}/subjects")
    public List<Subject> getSubjectsBySemesterId(@PathVariable("semesterId") UUID semesterId) {
        return subjectService.getSubjectsBySemesterId(semesterId);
    }

    @GetMapping("/subjects/{id}")
    public Subject getSubjectById(@PathVariable("id") UUID id) {
        return subjectService.getSubjectById(id);
    }

    @PatchMapping("/subjects/{id}")
    public Subject updateSubject(@PathVariable("id") UUID id, @RequestBody Subject updates) {
        return subjectService.updateSubject(id, updates);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/subjects/{id}")
    public void deleteSubject(@PathVariable("id") UUID id) {
        subjectService.deleteSubject(id);
    }
}