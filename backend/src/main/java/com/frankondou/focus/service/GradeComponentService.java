package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeComponent;
import java.util.List;
import java.util.UUID;

public interface GradeComponentService {
    GradeComponent createGradeComponent(UUID subjectId, GradeComponent component);
    List<GradeComponent> getGradeComponentsBySubjectId(UUID subjectId);
    GradeComponent getGradeComponentById(UUID id);
    GradeComponent updateGradeComponent(UUID id, GradeComponent updates);
    void deleteGradeComponent(UUID id);
}
