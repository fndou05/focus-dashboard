package com.frankondou.focus.controller;

import com.frankondou.focus.entity.User;
import com.frankondou.focus.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") UUID userId) {
        return userService.getUserById(userId);
    }

    @PatchMapping("/users/{id}")
    public User updateUser(@PathVariable("id") UUID id, @RequestBody User updates) {
        return userService.updateUser(id, updates);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
    }
}