package com.frankondou.focus.service;

import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.entity.enums.SessionStatus;
import com.frankondou.focus.exception.ConflictException;
import com.frankondou.focus.exception.StudySessionNotFoundException;
import com.frankondou.focus.exception.SubjectNotFoundException;
import com.frankondou.focus.repository.StudySessionRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;
    private final Clock clock;

    public StudySessionServiceImpl(
            StudySessionRepository studySessionRepository,
            SubjectRepository subjectRepository,
            Clock clock) {
        this.studySessionRepository = studySessionRepository;
        this.subjectRepository = subjectRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public StudySession startStudySession(UUID subjectId, StudySession session) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(SubjectNotFoundException::new);

        UUID userId = subject.getSemester().getUser().getId();

        if (studySessionRepository.existsBySubject_Semester_User_IdAndStatus(userId, SessionStatus.ACTIVE)) {
            throw new ConflictException("This user already has an active study session");
        }

        session.setSubject(subject);
        session.setStartedAt(Instant.now(clock));
        session.setStatus(SessionStatus.ACTIVE);
        session.setFocusedMinutes(null);
        session.setEndedAt(null);

        return studySessionRepository.save(session);
    }

    @Override
    @Transactional
    public StudySession endStudySession(UUID sessionId) {
        StudySession session = getStudySessionById(sessionId);

        if (session.getStatus() != SessionStatus.ACTIVE) {
            throw new ConflictException("Only active study sessions can be ended");
        }

        Instant endedAt = Instant.now(clock);
        long minutes = Duration.between(session.getStartedAt(), endedAt).toMinutes();

        session.setEndedAt(endedAt);
        session.setStatus(SessionStatus.COMPLETED);
        session.setFocusedMinutes(Math.toIntExact(minutes));

        return studySessionRepository.save(session);
    }

    @Override
    public List<StudySession> getStudySessionsBySubjectId(UUID subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new SubjectNotFoundException();
        }
        return studySessionRepository.findBySubject_Id(subjectId);
    }

    @Override
    public StudySession getStudySessionById(UUID id) {
        return studySessionRepository.findById(id)
                .orElseThrow(StudySessionNotFoundException::new);
    }

    @Override
    public void deleteStudySession(UUID id) {
        StudySession existing = getStudySessionById(id);
        studySessionRepository.delete(existing);
    }
}