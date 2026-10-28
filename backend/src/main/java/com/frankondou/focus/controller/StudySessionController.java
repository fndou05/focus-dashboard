package com.frankondou.focus.controller;

import com.frankondou.focus.dto.StartStudySessionRequest;
import com.frankondou.focus.dto.StudySessionResponse;
import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.service.StudySessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
public class StudySessionController {

    private final StudySessionService studySessionService;

    public StudySessionController(StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subjects/{subjectId}/study-sessions")
    public StudySessionResponse startStudySession(@PathVariable("subjectId") UUID subjectId,
                                                  @RequestBody @Valid StartStudySessionRequest request) {
        StudySession session = new StudySession();
        session.setMode(request.mode());
        return StudySessionResponse.from(studySessionService.startStudySession(subjectId, session));
    }

    @PatchMapping("/study-sessions/{id}/end")
    public StudySessionResponse endStudySession(@PathVariable("id") UUID id) {
        return StudySessionResponse.from(studySessionService.endStudySession(id));
    }

    @GetMapping("/subjects/{subjectId}/study-sessions")
    public List<StudySessionResponse> getStudySessionsBySubjectId(@PathVariable("subjectId") UUID subjectId) {
        return studySessionService.getStudySessionsBySubjectId(subjectId).stream()
                .map(StudySessionResponse::from)
                .toList();
    }

    @GetMapping("/study-sessions/{id}")
    public StudySessionResponse getStudySessionById(@PathVariable("id") UUID id) {
        return StudySessionResponse.from(studySessionService.getStudySessionById(id));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/study-sessions/{id}")
    public void deleteStudySession(@PathVariable("id") UUID id) {
        studySessionService.deleteStudySession(id);
    }
}