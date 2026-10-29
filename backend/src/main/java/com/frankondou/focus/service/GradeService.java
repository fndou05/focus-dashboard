package com.frankondou.focus.service;

import com.frankondou.focus.dto.CumulativeGpaResponse;
import com.frankondou.focus.dto.SemesterGpaResponse;
import com.frankondou.focus.dto.SubjectGradeResponse;
import java.util.UUID;

public interface GradeService {
    SubjectGradeResponse getSubjectGrade(UUID subjectId);
    SemesterGpaResponse getSemesterGpa(UUID semesterId);
    CumulativeGpaResponse getCumulativeGpa(UUID userId);
}
