package com.frankondou.focus.controller;

import com.frankondou.focus.dto.FreeTimeResponse;
import com.frankondou.focus.service.FreeTimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class FreeTimeController {

    private final FreeTimeService freeTimeService;

    public FreeTimeController(FreeTimeService freeTimeService) {
        this.freeTimeService = freeTimeService;
    }

    @GetMapping("/users/{userId}/free-time")
    public FreeTimeResponse getFreeTime(@PathVariable("userId") UUID userId) {
        return freeTimeService.getFreeTime(userId);
    }
}
