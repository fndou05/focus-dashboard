package com.frankondou.focus.service;

import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.SemesterRepository;
import com.frankondou.focus.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SemesterServiceImplTest {

    private final SemesterRepository semesterRepository = mock(SemesterRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final SemesterServiceImpl service = new SemesterServiceImpl(semesterRepository, userRepository);

    private Semester semester(LocalDate start, LocalDate end) {
        Semester semester = new Semester();
        semester.setName("Fall 2026");
        semester.setStartDate(start);
        semester.setEndDate(end);
        return semester;
    }

    @Test
    void createSemester_rejectsEndDateBeforeStartDate() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User("a@b.com", "Franko", "America/New_York")));

        Semester backwards = semester(LocalDate.of(2026, 12, 20), LocalDate.of(2026, 9, 1));

        assertThrows(BadRequestException.class, () -> service.createSemester(userId, backwards));
        verify(semesterRepository, never()).save(any());
    }

    @Test
    void createSemester_attachesUserAndSaves() {
        UUID userId = UUID.randomUUID();
        User user = new User("a@b.com", "Franko", "America/New_York");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(semesterRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        Semester result = service.createSemester(userId,
                semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 20)));

        assertSame(user, result.getUser());
        verify(semesterRepository).save(any());
    }

    @Test
    void createSemester_unknownUser_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.createSemester(userId,
                semester(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 20))));
    }
}