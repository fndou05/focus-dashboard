package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CreateSubjectRequest;
import com.frankondou.focus.dto.SubjectResponse;
import com.frankondou.focus.dto.UpdateSubjectRequest;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    public SubjectResponse createSubject(@PathVariable("semesterId") UUID semesterId,
                                         @RequestBody @Valid CreateSubjectRequest request) {
        Subject subject = new Subject();
        subject.setName(request.name());
        subject.setCredits(request.credits());
        subject.setSelfRatedStrength(request.selfRatedStrength());
        return SubjectResponse.from(subjectService.createSubject(semesterId, subject));
    }

    @GetMapping("/semesters/{semesterId}/subjects")
    public List<SubjectResponse> getSubjectsBySemesterId(@PathVariable("semesterId") UUID semesterId) {
        return subjectService.getSubjectsBySemesterId(semesterId).stream()
                .map(SubjectResponse::from)
                .toList();
    }

    @GetMapping("/subjects/{id}")
    public SubjectResponse getSubjectById(@PathVariable("id") UUID id) {
        return SubjectResponse.from(subjectService.getSubjectById(id));
    }

    @PatchMapping("/subjects/{id}")
    public SubjectResponse updateSubject(@PathVariable("id") UUID id,
                                         @RequestBody @Valid UpdateSubjectRequest request) {
        Subject updates = new Subject();
        updates.setName(request.name());
        updates.setCredits(request.credits());
        updates.setSelfRatedStrength(request.selfRatedStrength());
        updates.setFinalLetterGrade(request.finalLetterGrade());
        return SubjectResponse.from(subjectService.updateSubject(id, updates));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/subjects/{id}")
    public void deleteSubject(@PathVariable("id") UUID id) {
        subjectService.deleteSubject(id);
    }
}