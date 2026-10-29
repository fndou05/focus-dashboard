package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CheckInResponse;
import com.frankondou.focus.dto.CreateCheckInRequest;
import com.frankondou.focus.entity.CheckIn;
import com.frankondou.focus.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/study-sessions/{sessionId}/check-in")
    public CheckInResponse createCheckIn(@PathVariable("sessionId") UUID sessionId,
                                         @RequestBody @Valid CreateCheckInRequest request) {
        CheckIn checkIn = new CheckIn();
        checkIn.setMoodRating(request.moodRating());
        checkIn.setDifficultyRating(request.difficultyRating());
        checkIn.setConfidenceRating(request.confidenceRating());
        checkIn.setNotes(request.notes());
        return CheckInResponse.from(checkInService.createCheckIn(sessionId, checkIn));
    }

    @GetMapping("/study-sessions/{sessionId}/check-in")
    public CheckInResponse getCheckInBySessionId(@PathVariable("sessionId") UUID sessionId) {
        return CheckInResponse.from(checkInService.getCheckInBySessionId(sessionId));
    }

    @GetMapping("/subjects/{subjectId}/check-ins")
    public List<CheckInResponse> getCheckInsBySubjectId(@PathVariable("subjectId") UUID subjectId) {
        return checkInService.getCheckInsBySubjectId(subjectId).stream()
                .map(CheckInResponse::from)
                .toList();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/check-ins/{id}")
    public void deleteCheckIn(@PathVariable("id") UUID id) {
        checkInService.deleteCheckIn(id);
    }
}
