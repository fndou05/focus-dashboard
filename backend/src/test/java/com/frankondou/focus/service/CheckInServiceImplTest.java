package com.frankondou.focus.service;

import com.frankondou.focus.entity.CheckIn;
import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.entity.enums.SessionMode;
import com.frankondou.focus.entity.enums.SessionStatus;
import com.frankondou.focus.exception.CheckInNotFoundException;
import com.frankondou.focus.exception.ConflictException;
import com.frankondou.focus.exception.StudySessionNotFoundException;
import com.frankondou.focus.repository.CheckInRepository;
import com.frankondou.focus.repository.StudySessionRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckInServiceImplTest {

    private final CheckInRepository checkInRepository = mock(CheckInRepository.class);
    private final StudySessionRepository studySessionRepository = mock(StudySessionRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final CheckInServiceImpl service =
            new CheckInServiceImpl(checkInRepository, studySessionRepository, subjectRepository);

    private final UUID sessionId = UUID.randomUUID();

    private StudySession session(SessionStatus status) {
        StudySession session = new StudySession(null, SessionMode.FIXED, Instant.parse("2026-10-26T03:30:00Z"));
        session.setStatus(status);
        return session;
    }

    @Test
    void createCheckIn_onActiveSession_throwsConflict() {
        StudySession active = session(SessionStatus.ACTIVE);
        when(studySessionRepository.findById(sessionId)).thenReturn(Optional.of(active));

        assertThrows(ConflictException.class, () -> service.createCheckIn(sessionId, new CheckIn()));
        verify(checkInRepository, never()).saveAndFlush(any());
    }

    @Test
    void createCheckIn_onCompletedSession_savesWithSessionAttached() {
        StudySession completed = session(SessionStatus.COMPLETED);
        when(studySessionRepository.findById(sessionId)).thenReturn(Optional.of(completed));
        when(checkInRepository.existsBySession_Id(sessionId)).thenReturn(false);
        when(checkInRepository.saveAndFlush(any())).thenAnswer(call -> call.getArgument(0));

        CheckIn saved = service.createCheckIn(sessionId, new CheckIn());

        assertSame(completed, saved.getSession());
    }

    @Test
    void createCheckIn_whenSessionAlreadyHasOne_throwsConflict() {
        StudySession completed = session(SessionStatus.COMPLETED);
        when(studySessionRepository.findById(sessionId)).thenReturn(Optional.of(completed));
        when(checkInRepository.existsBySession_Id(sessionId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createCheckIn(sessionId, new CheckIn()));
        verify(checkInRepository, never()).saveAndFlush(any());
    }

    @Test
    void createCheckIn_forUnknownSession_throwsNotFound() {
        when(studySessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(StudySessionNotFoundException.class, () -> service.createCheckIn(sessionId, new CheckIn()));
    }

    @Test
    void getCheckInBySessionId_whenSessionHasNone_throwsNotFound() {
        when(studySessionRepository.existsById(sessionId)).thenReturn(true);
        when(checkInRepository.findBySession_Id(sessionId)).thenReturn(Optional.empty());

        assertThrows(CheckInNotFoundException.class, () -> service.getCheckInBySessionId(sessionId));
    }

    @Test
    void deleteCheckIn_forUnknownCheckIn_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(checkInRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CheckInNotFoundException.class, () -> service.deleteCheckIn(id));
        verify(checkInRepository, never()).delete(any());
    }
}
