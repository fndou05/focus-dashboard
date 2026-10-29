package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CreateResponsibilityRequest;
import com.frankondou.focus.dto.ResponsibilityResponse;
import com.frankondou.focus.dto.UpdateResponsibilityRequest;
import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.service.ResponsibilityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ResponsibilityController {

    private final ResponsibilityService responsibilityService;

    public ResponsibilityController(ResponsibilityService responsibilityService) {
        this.responsibilityService = responsibilityService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/responsibilities")
    public ResponsibilityResponse createResponsibility(@PathVariable("userId") UUID userId,
                                                       @RequestBody @Valid CreateResponsibilityRequest request) {
        Responsibility responsibility = new Responsibility();
        responsibility.setLabel(request.label());
        responsibility.setCategory(request.category());
        responsibility.setHoursPerWeek(request.hoursPerWeek());
        return ResponsibilityResponse.from(responsibilityService.createResponsibility(userId, responsibility));
    }

    @GetMapping("/users/{userId}/responsibilities")
    public List<ResponsibilityResponse> getResponsibilitiesByUserId(@PathVariable("userId") UUID userId) {
        return responsibilityService.getResponsibilitiesByUserId(userId).stream()
                .map(ResponsibilityResponse::from)
                .toList();
    }

    @GetMapping("/responsibilities/{id}")
    public ResponsibilityResponse getResponsibilityById(@PathVariable("id") UUID id) {
        return ResponsibilityResponse.from(responsibilityService.getResponsibilityById(id));
    }

    @PatchMapping("/responsibilities/{id}")
    public ResponsibilityResponse updateResponsibility(@PathVariable("id") UUID id,
                                                       @RequestBody @Valid UpdateResponsibilityRequest request) {
        Responsibility updates = new Responsibility();
        updates.setLabel(request.label());
        updates.setCategory(request.category());
        updates.setHoursPerWeek(request.hoursPerWeek());
        return ResponsibilityResponse.from(responsibilityService.updateResponsibility(id, updates));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/responsibilities/{id}")
    public void deleteResponsibility(@PathVariable("id") UUID id) {
        responsibilityService.deleteResponsibility(id);
    }
}
