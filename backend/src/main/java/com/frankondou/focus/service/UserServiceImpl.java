package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeCutoff;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.ConflictException;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private record DefaultCutoff(String letter, String minPercent, String gradePoints) {}

    private static final List<DefaultCutoff> DEFAULT_SCALE = List.of(
            new DefaultCutoff("A",  "93", "4.0"),
            new DefaultCutoff("A-", "90", "3.7"),
            new DefaultCutoff("B+", "87", "3.3"),
            new DefaultCutoff("B",  "83", "3.0"),
            new DefaultCutoff("B-", "80", "2.7"),
            new DefaultCutoff("C+", "77", "2.3"),
            new DefaultCutoff("C",  "73", "2.0"),
            new DefaultCutoff("C-", "70", "1.7"),
            new DefaultCutoff("D+", "67", "1.3"),
            new DefaultCutoff("D",  "63", "1.0"),
            new DefaultCutoff("D-", "60", "0.7"),
            new DefaultCutoff("F",  "0",  "0.0")
    );

    private final UserRepository userRepository;
    private final GradeCutoffRepository gradeCutoffRepository;

    public UserServiceImpl(UserRepository userRepository, GradeCutoffRepository gradeCutoffRepository) {
        this.userRepository = userRepository;
        this.gradeCutoffRepository = gradeCutoffRepository;
    }

    // Transactional: if the grade scale fails to save, the user isn't saved either
    @Override
    @Transactional
    public User createUser(User user) {
        validateTimezone(user.getTimezone());
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        User saved = userRepository.save(user);

        gradeCutoffRepository.saveAll(DEFAULT_SCALE.stream()
                .map(c -> new GradeCutoff(saved, c.letter(),
                        new BigDecimal(c.minPercent()), new BigDecimal(c.gradePoints())))
                .toList());

        return saved;
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

        if (updates.getDisplayName() != null && !updates.getDisplayName().isBlank()) {
            existing.setDisplayName(updates.getDisplayName());
        }
        if (updates.getTimezone() != null && !updates.getTimezone().isBlank()) {
            validateTimezone(updates.getTimezone());
            existing.setTimezone(updates.getTimezone());
        }

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(UUID id) {
        User existing = getUserById(id);
        userRepository.delete(existing);
    }

    private void validateTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new BadRequestException("Invalid timezone: " + timezone);
        }
    }
}