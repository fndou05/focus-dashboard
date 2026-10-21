package com.frankondou.focus.service;

import com.frankondou.focus.entity.Semester;
import java.util.List;
import java.util.UUID;

public interface SemesterService {
    Semester createSemester(UUID userId, Semester semester);
    List<Semester> getSemestersByUserId(UUID userId);
    Semester getSemesterById(UUID id);
    Semester updateSemester(UUID id, Semester updates);
    void deleteSemester(UUID id);
}
