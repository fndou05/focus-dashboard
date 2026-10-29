package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeEntry;
import java.util.List;
import java.util.UUID;

public interface GradeEntryService {
    GradeEntry createEntry(UUID componentId, GradeEntry entry);
    List<GradeEntry> getEntriesByComponentId(UUID componentId);
    GradeEntry getEntryById(UUID id);
    GradeEntry updateEntry(UUID id, GradeEntry updates);
    void deleteEntry(UUID id);
}
