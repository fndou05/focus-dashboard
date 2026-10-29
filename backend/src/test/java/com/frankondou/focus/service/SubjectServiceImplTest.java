package com.frankondou.focus.service;

import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.enums.SubjectCategory;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.SemesterRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

    @Test
    void updateSubject_withNoClassHours_leavesTheExistingValue() {
        existing.setWeeklyClassHours(new BigDecimal("2"));
        Subject updates = new Subject();
        updates.setWeeklyClassHours(null);

        Subject result = service.updateSubject(subjectId, updates);

        assertEquals(new BigDecimal("2"), result.getWeeklyClassHours());
    }

    @Test
    void updateSubject_withClassHours_setsThem() {
        Subject updates = new Subject();
        updates.setWeeklyClassHours(new BigDecimal("3.5"));

        Subject result = service.updateSubject(subjectId, updates);

        assertEquals(new BigDecimal("3.5"), result.getWeeklyClassHours());
    }

    @Test
    void updateSubject_withNoCategory_leavesTheExistingCategory() {
        existing.setCategory(SubjectCategory.STEM);
        Subject updates = new Subject();
        updates.setCategory(null);

        Subject result = service.updateSubject(subjectId, updates);

        assertEquals(SubjectCategory.STEM, result.getCategory());
    }

    @Test
    void updateSubject_withCategory_setsIt() {
        Subject updates = new Subject();
        updates.setCategory(SubjectCategory.HEALTH);

        Subject result = service.updateSubject(subjectId, updates);

        assertEquals(SubjectCategory.HEALTH, result.getCategory());
    }
}
