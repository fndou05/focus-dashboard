package com.frankondou.focus.service;

import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.ConflictException;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final GradeCutoffRepository gradeCutoffRepository = mock(GradeCutoffRepository.class);
    private final UserServiceImpl service = new UserServiceImpl(userRepository, gradeCutoffRepository);

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
}

