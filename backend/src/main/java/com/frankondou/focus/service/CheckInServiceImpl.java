package com.frankondou.focus.service;

import com.frankondou.focus.entity.CheckIn;
import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.entity.enums.SessionStatus;
import com.frankondou.focus.exception.CheckInNotFoundException;
import com.frankondou.focus.exception.ConflictException;
import com.frankondou.focus.exception.StudySessionNotFoundException;
import com.frankondou.focus.exception.SubjectNotFoundException;
import com.frankondou.focus.repository.CheckInRepository;
import com.frankondou.focus.repository.StudySessionRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepository checkInRepository;
    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;

    public CheckInServiceImpl(CheckInRepository checkInRepository,
                              StudySessionRepository studySessionRepository,
                              SubjectRepository subjectRepository) {
        this.checkInRepository = checkInRepository;
        this.studySessionRepository = studySessionRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public CheckIn createCheckIn(UUID sessionId, CheckIn checkIn) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(StudySessionNotFoundException::new);

        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new ConflictException("Check-ins can only be added to completed sessions");
        }
        if (checkInRepository.existsBySession_Id(sessionId)) {
            throw new ConflictException("This session already has a check-in");
        }

        checkIn.setSession(session);
        // Flush now so a duplicate from a simultaneous request fails here as a 409, not as a 500
        try {
            return checkInRepository.saveAndFlush(checkIn);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("This session already has a check-in");
        }
    }

    @Override
    public CheckIn getCheckInBySessionId(UUID sessionId) {
        if (!studySessionRepository.existsById(sessionId)) {
            throw new StudySessionNotFoundException();
        }

        return checkInRepository.findBySession_Id(sessionId)
                .orElseThrow(CheckInNotFoundException::new);
    }

    @Override
    public List<CheckIn> getCheckInsBySubjectId(UUID subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new SubjectNotFoundException();
        }

        return checkInRepository.findBySubjectIdNewestFirst(subjectId);
    }

    @Override
    public void deleteCheckIn(UUID id) {
        CheckIn existing = checkInRepository.findById(id)
                .orElseThrow(CheckInNotFoundException::new);
        checkInRepository.delete(existing);
    }
}
