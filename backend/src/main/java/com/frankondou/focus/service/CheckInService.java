package com.frankondou.focus.service;

import com.frankondou.focus.entity.CheckIn;
import java.util.List;
import java.util.UUID;

public interface CheckInService {
    CheckIn createCheckIn(UUID sessionId, CheckIn checkIn);
    CheckIn getCheckInBySessionId(UUID sessionId);
    List<CheckIn> getCheckInsBySubjectId(UUID subjectId);
    void deleteCheckIn(UUID id);
}
