package com.frankondou.focus.service;

import com.frankondou.focus.dto.SubjectGradeResponse;
import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.entity.GradeCutoff;
import com.frankondou.focus.entity.GradeEntry;
import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.enums.GradeCategory;
import com.frankondou.focus.repository.GradeComponentRepository;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.GradeEntryRepository;
import com.frankondou.focus.repository.SemesterRepository;
import com.frankondou.focus.repository.SubjectRepository;
import com.frankondou.focus.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeServiceImplTest {

    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final SemesterRepository semesterRepository = mock(SemesterRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final GradeComponentRepository gradeComponentRepository = mock(GradeComponentRepository.class);
    private final GradeEntryRepository gradeEntryRepository = mock(GradeEntryRepository.class);
    private final GradeCutoffRepository gradeCutoffRepository = mock(GradeCutoffRepository.class);
    private final GradeServiceImpl service = new GradeServiceImpl(subjectRepository, semesterRepository,
            userRepository, gradeComponentRepository, gradeEntryRepository, gradeCutoffRepository);

    private final UUID userId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();
    private final GradeCutoff cutoffA = cutoff("A", "93", "4.00");
    private final GradeCutoff cutoffB = cutoff("B", "83", "3.00");
    private final GradeCutoff cutoffF = cutoff("F", "0", "0.00");

    private GradeCutoff cutoff(String letter, String minPercent, String gradePoints) {
        GradeCutoff cutoff = new GradeCutoff();
        cutoff.setLetter(letter);
        cutoff.setMinPercent(new BigDecimal(minPercent));
        cutoff.setGradePoints(new BigDecimal(gradePoints));
        return cutoff;
    }

    private GradeEntry entry(String earned, String possible) {
        GradeEntry entry = new GradeEntry();
        entry.setScoreEarned(new BigDecimal(earned));
        entry.setScorePossible(new BigDecimal(possible));
        return entry;
    }

    private GradeComponent component(String name, GradeCategory category, String weight, List<GradeEntry> entries) {
        GradeComponent component = mock(GradeComponent.class);
        UUID id = UUID.randomUUID();
        when(component.getId()).thenReturn(id);
        when(component.getName()).thenReturn(name);
        when(component.getCategory()).thenReturn(category);
        when(component.getWeightPercent()).thenReturn(new BigDecimal(weight));
        when(gradeEntryRepository.findByComponent_Id(id)).thenReturn(entries);
        return component;
    }

    // Physics, 4 credits, graded with Homework 20 / Midterm 30 / Final 50
    private void givenPhysics(String finalLetter, boolean withEntries) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        Semester semester = mock(Semester.class);
        when(semester.getUser()).thenReturn(user);
        Subject subject = mock(Subject.class);
        when(subject.getId()).thenReturn(subjectId);
        when(subject.getName()).thenReturn("Physics");
        when(subject.getCredits()).thenReturn(new BigDecimal("4"));
        when(subject.getSemester()).thenReturn(semester);
        when(subject.getFinalLetterGrade()).thenReturn(finalLetter);
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        when(gradeCutoffRepository.findByUser_IdOrderByMinPercentDesc(userId))
                .thenReturn(List.of(cutoffA, cutoffB, cutoffF));
        when(gradeCutoffRepository.findByUser_IdAndLetter(userId, "A")).thenReturn(Optional.of(cutoffA));

        List<GradeEntry> homework = withEntries ? List.of(entry("48", "50")) : List.of();
        List<GradeEntry> midterm = withEntries ? List.of(entry("80", "100")) : List.of();
        List<GradeComponent> components = List.of(
                component("Homework", GradeCategory.HOMEWORK, "20", homework),
                component("Midterm", GradeCategory.MIDTERM, "30", midterm),
                component("Final", GradeCategory.FINAL, "50", List.of()));
        when(gradeComponentRepository.findBySubject_Id(subjectId)).thenReturn(components);
    }

    @Test
    void getSubjectGrade_onlyWeightsComponentsThatHaveEntries() {
        givenPhysics(null, true);

        SubjectGradeResponse result = service.getSubjectGrade(subjectId);

        assertEquals(new BigDecimal("86.40"), result.currentPercent());
        assertEquals("B", result.letter());
        assertEquals(new BigDecimal("3.00"), result.gradePoints());
        assertFalse(result.isFinal());
    }

    @Test
    void getSubjectGrade_finalLetterOverridesTheCalculatedOne() {
        givenPhysics("A", true);

        SubjectGradeResponse result = service.getSubjectGrade(subjectId);

        assertEquals("A", result.letter());
        assertEquals(new BigDecimal("4.00"), result.gradePoints());
        assertTrue(result.isFinal());
        assertEquals(new BigDecimal("86.40"), result.currentPercent());
    }

    @Test
    void getSubjectGrade_withNoEntries_hasNoGrade() {
        givenPhysics(null, false);

        SubjectGradeResponse result = service.getSubjectGrade(subjectId);

        assertNull(result.currentPercent());
        assertNull(result.letter());
        assertNull(result.gradePoints());
    }
}
