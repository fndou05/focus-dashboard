package com.frankondou.focus.repository;

import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudySessionRepository
        extends JpaRepository<StudySession, UUID> {

    List<StudySession> findBySubject_Id(UUID subjectId);

    boolean existsBySubject_Semester_User_IdAndStatus(
            UUID userId, SessionStatus status);

    Optional<StudySession> findFirstBySubject_Semester_User_IdAndStatus(
            UUID userId, SessionStatus status);
}