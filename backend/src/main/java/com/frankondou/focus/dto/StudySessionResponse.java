package com.frankondou.focus.dto;

import com.frankondou.focus.entity.StudySession;
import com.frankondou.focus.entity.enums.SessionMode;
import com.frankondou.focus.entity.enums.SessionStatus;
import java.time.Instant;
import java.util.UUID;

public record StudySessionResponse(UUID id, UUID subjectId, SessionMode mode, SessionStatus status,
                                   Integer focusedMinutes, Instant startedAt, Instant endedAt) {

    public static StudySessionResponse from(StudySession session) {
        return new StudySessionResponse(
                session.getId(),
                session.getSubject().getId(),
                session.getMode(),
                session.getStatus(),
                session.getFocusedMinutes(),
                session.getStartedAt(),
                session.getEndedAt()
        );
    }
}