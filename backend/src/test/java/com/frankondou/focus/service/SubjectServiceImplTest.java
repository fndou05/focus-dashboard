package com.frankondou.focus.service;

import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.SemesterRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubjectServiceImplTest {

    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final SemesterRepository semesterRepository = mock(SemesterRepository.class);
    private final GradeCutoffRepository gradeCutoffRepository = mock(GradeCutoffRepository.class);
    private final SubjectServiceImpl service =
            new SubjectServiceImpl(subjectRepository, semesterRepository, gradeCutoffRepository);

    private final UUID subjectId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final Subject existing = new Subject();

    SubjectServiceImplTest() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        Semester semester = mock(Semester.class);
        when(semester.getUser()).thenReturn(user);
        existing.setSemester(semester);
        existing.setFinalLetterGrade("A");
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(existing));
        when(subjectRepository.save(any())).thenAnswer(call -> call.getArgument(0));
    }

    private Subject finalGradeUpdate(String letter) {
        Subject updates = new Subject();
        updates.setFinalLetterGrade(letter);
        return updates;
    }

    @Test
    void updateSubject_withUnknownFinalLetter_throwsBadRequest() {
        when(gradeCutoffRepository.existsByUser_IdAndLetter(userId, "Z")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> service.updateSubject(subjectId, finalGradeUpdate("Z")));
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void updateSubject_storesTheFinalLetterTrimmedAndUppercased() {
        when(gradeCutoffRepository.existsByUser_IdAndLetter(userId, "B+")).thenReturn(true);

        Subject result = service.updateSubject(subjectId, finalGradeUpdate(" b+ "));

        assertEquals("B+", result.getFinalLetterGrade());
    }

    @Test
    void updateSubject_withEmptyFinalLetter_clearsIt() {
        Subject result = service.updateSubject(subjectId, finalGradeUpdate(""));

        assertNull(result.getFinalLetterGrade());
    }
}
