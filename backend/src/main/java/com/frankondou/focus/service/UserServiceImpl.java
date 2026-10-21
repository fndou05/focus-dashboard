package com.frankondou.focus.service;

import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    // Partial update: only fields that were sent get changed
    @Override
    public User updateUser(UUID id, User updates) {
        User existing = getUserById(id);

        if (updates.getEmail() != null
                && !updates.getEmail().isBlank()) {
            existing.setEmail(updates.getEmail());
        }

        if (updates.getDisplayName() != null
                && !updates.getDisplayName().isBlank()) {
            existing.setDisplayName(updates.getDisplayName());
        }

        if (updates.getTimezone() != null
                && !updates.getTimezone().isBlank()) {
            existing.setTimezone(updates.getTimezone());
        }

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(UUID id) {
        User existing = getUserById(id);
        userRepository.delete(existing);
    }
}