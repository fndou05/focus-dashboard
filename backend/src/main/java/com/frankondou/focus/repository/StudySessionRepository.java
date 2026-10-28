package com.frankondou.focus.repository;

import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, UUID> {

    List<StudySession> findBySubject_Id(UUID subjectId);

    boolean existsBySubject_Semester_User_IdAndStatus(UUID userId, SessionStatus status);

    @Query("""
            select new com.frankondou.focus.repository.SubjectMinutes(s.subject.id, sum(s.focusedMinutes))
            from StudySession s
            where s.subject.id in :subjectIds
              and s.status = :status
              and s.startedAt >= :weekStart
              and s.startedAt < :weekEnd
            group by s.subject.id
            """)
    List<SubjectMinutes> sumFocusedMinutesBySubject(@Param("subjectIds") List<UUID> subjectIds,
                                                    @Param("status") SessionStatus status,
                                                    @Param("weekStart") Instant weekStart,
                                                    @Param("weekEnd") Instant weekEnd);
}