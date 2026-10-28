package com.frankondou.focus.controller;

import com.frankondou.focus.dto.CreateUserRequest;
import com.frankondou.focus.dto.UpdateUserRequest;
import com.frankondou.focus.dto.UserResponse;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = new User(request.email(), request.displayName(), request.timezone());
        return UserResponse.from(userService.createUser(user));
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable("id") UUID id) {
        return UserResponse.from(userService.getUserById(id));
    }

    @PatchMapping("/users/{id}")
    public UserResponse updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UpdateUserRequest request) {
        User updates = new User(null, request.displayName(), request.timezone());
        return UserResponse.from(userService.updateUser(id, updates));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
    }
}