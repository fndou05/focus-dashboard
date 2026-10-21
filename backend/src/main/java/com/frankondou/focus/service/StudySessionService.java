package com.frankondou.focus.service;

import com.frankondou.focus.entity.StudySession;
import java.util.List;
import java.util.UUID;

public interface StudySessionService {
    StudySession startStudySession(UUID subjectId, StudySession session);
    StudySession endStudySession(UUID sessionId);
    List<StudySession> getStudySessionsBySubjectId(UUID subjectId);
    StudySession getStudySessionById(UUID id);
    void deleteStudySession(UUID id);
}
