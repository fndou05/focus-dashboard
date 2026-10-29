package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.enums.GradeCategory;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.SubjectNotFoundException;
import com.frankondou.focus.repository.GradeComponentRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GradeComponentServiceImplTest {

    private final GradeComponentRepository gradeComponentRepository = mock(GradeComponentRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final GradeComponentServiceImpl service =
            new GradeComponentServiceImpl(gradeComponentRepository, subjectRepository);

    private final UUID subjectId = UUID.randomUUID();

    // Mocks are built before each when(...), since Mockito can't create one in the middle of a stubbing
    // Ids come from the database, so a mock supplies a fixed one
    private GradeComponent existing(String weight) {
        GradeComponent component = mock(GradeComponent.class);
        when(component.getId()).thenReturn(UUID.randomUUID());
        when(component.getWeightPercent()).thenReturn(new BigDecimal(weight));
        return component;
    }

    private GradeComponent newComponent(String weight) {
        return new GradeComponent(null, "Final", GradeCategory.FINAL, new BigDecimal(weight));
    }

    @Test
    void createComponent_whenTotalWouldExceed100_throwsBadRequest() {
        List<GradeComponent> others = List.of(existing("50"), existing("30"));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(new Subject()));
        when(gradeComponentRepository.findBySubject_Id(subjectId)).thenReturn(others);

        assertThrows(BadRequestException.class,
                () -> service.createGradeComponent(subjectId, newComponent("30")));
        verify(gradeComponentRepository, never()).save(any());
    }

    @Test
    void createComponent_whenTotalIsExactly100_savesAndAttachesSubject() {
        Subject subject = new Subject();
        List<GradeComponent> others = List.of(existing("80"));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(gradeComponentRepository.findBySubject_Id(subjectId)).thenReturn(others);
        when(gradeComponentRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        GradeComponent saved = service.createGradeComponent(subjectId, newComponent("20"));

        assertSame(subject, saved.getSubject());
    }

    @Test
    void updateComponent_excludesItsOwnOldWeightFromTheTotal() {
        Subject subject = mock(Subject.class);
        when(subject.getId()).thenReturn(subjectId);
        GradeComponent target = existing("20");
        when(target.getSubject()).thenReturn(subject);
        List<GradeComponent> all = List.of(target, existing("60"));
        when(gradeComponentRepository.findById(target.getId())).thenReturn(Optional.of(target));
        // Others total 60; counting the target's old 20 as well would make 40 push it over 100
        when(gradeComponentRepository.findBySubject_Id(subjectId)).thenReturn(all);
        when(gradeComponentRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        service.updateGradeComponent(target.getId(), new GradeComponent(null, null, null, new BigDecimal("40")));

        verify(target).setWeightPercent(new BigDecimal("40"));
        verify(gradeComponentRepository).save(target);
    }

    @Test
    void updateComponent_whenOthersPlusNewWeightExceed100_throwsBadRequest() {
        Subject subject = mock(Subject.class);
        when(subject.getId()).thenReturn(subjectId);
        GradeComponent target = existing("20");
        when(target.getSubject()).thenReturn(subject);
        List<GradeComponent> all = List.of(target, existing("60"));
        when(gradeComponentRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(gradeComponentRepository.findBySubject_Id(subjectId)).thenReturn(all);

        GradeComponent updates = new GradeComponent(null, null, null, new BigDecimal("50"));

        assertThrows(BadRequestException.class, () -> service.updateGradeComponent(target.getId(), updates));
        verify(gradeComponentRepository, never()).save(any());
    }

    @Test
    void createComponent_whenSubjectDoesNotExist_throwsNotFound() {
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        assertThrows(SubjectNotFoundException.class,
                () -> service.createGradeComponent(subjectId, newComponent("20")));
    }
}
