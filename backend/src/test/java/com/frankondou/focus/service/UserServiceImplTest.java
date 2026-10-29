package com.frankondou.focus.service;

import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.ConflictException;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final GradeCutoffRepository gradeCutoffRepository = mock(GradeCutoffRepository.class);
    private final Instant now = Instant.parse("2026-10-29T12:00:00Z");
    private final Clock clock = Clock.fixed(now, ZoneOffset.UTC);
    private final UserServiceImpl service = new UserServiceImpl(userRepository, gradeCutoffRepository, clock);

    @Test
    void createUser_withDuplicateEmail_throwsConflict() {
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

        User user = new User("a@b.com", "Franko", "America/New_York");

        assertThrows(ConflictException.class, () -> service.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_withInvalidTimezone_throwsBadRequest() {
        User user = new User("a@b.com", "Franko", "Mars/Olympus");

        assertThrows(BadRequestException.class, () -> service.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void completeOnboarding_whenNotFinished_setsTheTimestampFromTheClock() {
        UUID id = UUID.randomUUID();
        User user = new User("a@b.com", "Franko", "America/New_York");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        User result = service.completeOnboarding(id);

        assertEquals(now, result.getOnboardingCompletedAt());
    }

    @Test
    void completeOnboarding_whenAlreadyFinished_keepsTheOriginalTimestamp() {
        UUID id = UUID.randomUUID();
        Instant earlier = Instant.parse("2026-10-01T09:00:00Z");
        User user = new User("a@b.com", "Franko", "America/New_York");
        user.setOnboardingCompletedAt(earlier);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = service.completeOnboarding(id);

        assertEquals(earlier, result.getOnboardingCompletedAt());
        verify(userRepository, never()).save(any());
    }

    @Test
    void completeOnboarding_forUnknownUser_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.completeOnboarding(id));
    }
}
