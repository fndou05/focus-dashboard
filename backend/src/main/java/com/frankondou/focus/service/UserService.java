package com.frankondou.focus.service;

import com.frankondou.focus.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(UUID id);
    User updateUser(UUID id, User updates);
    void deleteUser(UUID id);
}