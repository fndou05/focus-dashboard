package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.enums.GradeCategory;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.repository.GradeComponentRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GradeComponentServiceImplTest {

    private final GradeComponentRepository gradeComponentRepository = mock(GradeComponentRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final GradeComponentServiceImpl service =
            new GradeComponentServiceImpl(gradeComponentRepository, subjectRepository);

    // Ids come from the database, so the tests fake them
    private GradeComponent componentWithWeight(String weight) {
        GradeComponent component = spy(new GradeComponent(null, "Exam", GradeCategory.TEST, new BigDecimal(weight)));
        doReturn(UUID.randomUUID()).when(component).getId();
        return component;
    }

    @Test
    void createGradeComponent_whenTotalWeightWouldExceed100_throwsBadRequest() {
        UUID subjectId = UUID.randomUUID();
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(new Subject()));
        // Built before the stubbing: Mockito can't create spies in the middle of a when(...)
        List<GradeComponent> existing = List.of(componentWithWeight("60"), componentWithWeight("30"));
        when(gradeComponentRepository.findBySubject_Id(subjectId)).thenReturn(existing);

        GradeComponent newComponent = new GradeComponent(null, "Final", GradeCategory.FINAL, new BigDecimal("20"));

        assertThrows(BadRequestException.class, () -> service.createGradeComponent(subjectId, newComponent));
        verify(gradeComponentRepository, never()).save(any());
    }

    @Test
    void updateGradeComponent_doesNotCountItsOwnOldWeight() {
        GradeComponent existing = componentWithWeight("60");
        GradeComponent other = componentWithWeight("30");
        Subject subject = spy(new Subject());
        UUID subjectId = UUID.randomUUID();
        doReturn(subjectId).when(subject).getId();
        existing.setSubject(subject);
        when(gradeComponentRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(gradeComponentRepository.findBySubject_Id(subjectId)).thenReturn(List.of(existing, other));
        when(gradeComponentRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        GradeComponent updates = new GradeComponent(null, null, null, new BigDecimal("70"));

        service.updateGradeComponent(existing.getId(), updates);

        verify(gradeComponentRepository).save(existing);
    }
}
