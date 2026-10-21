package com.frankondou.focus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// Handles web requests; return values become the response body
@RestController
class HelloController {

    // GET /hello/{name} routes here
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) { // name is filled from the path
        return "Hello " + name;
    }
}