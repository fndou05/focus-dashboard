package com.frankondou.focus.service;

import com.frankondou.focus.entity.Subject;
import java.util.List;
import java.util.UUID;

public interface SubjectService {
    Subject createSubject(UUID semesterId, Subject subject);
    List<Subject> getSubjectsBySemesterId(UUID semesterId);
    Subject getSubjectById(UUID id);
    Subject updateSubject(UUID id, Subject updates);
    void deleteSubject(UUID id);
}
