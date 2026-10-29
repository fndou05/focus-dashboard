package com.frankondou.focus.service;

import com.frankondou.focus.entity.Responsibility;
import java.util.List;
import java.util.UUID;

public interface ResponsibilityService {
    Responsibility createResponsibility(UUID userId, Responsibility responsibility);
    List<Responsibility> getResponsibilitiesByUserId(UUID userId);
    Responsibility getResponsibilityById(UUID id);
    Responsibility updateResponsibility(UUID id, Responsibility updates);
    void deleteResponsibility(UUID id);
}
