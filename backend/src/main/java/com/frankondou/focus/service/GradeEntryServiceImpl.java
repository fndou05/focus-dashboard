package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.entity.GradeEntry;
import com.frankondou.focus.exception.GradeComponentNotFoundException;
import com.frankondou.focus.exception.GradeEntryNotFoundException;
import com.frankondou.focus.repository.GradeComponentRepository;
import com.frankondou.focus.repository.GradeEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GradeEntryServiceImpl implements GradeEntryService {

    private final GradeEntryRepository gradeEntryRepository;
    private final GradeComponentRepository gradeComponentRepository;

    public GradeEntryServiceImpl(GradeEntryRepository gradeEntryRepository,
                                 GradeComponentRepository gradeComponentRepository) {
        this.gradeEntryRepository = gradeEntryRepository;
        this.gradeComponentRepository = gradeComponentRepository;
    }

    @Override
    public GradeEntry createEntry(UUID componentId, GradeEntry entry) {
        GradeComponent component = gradeComponentRepository.findById(componentId)
                .orElseThrow(GradeComponentNotFoundException::new);

        entry.setComponent(component);
        return gradeEntryRepository.save(entry);
    }

    @Override
    public List<GradeEntry> getEntriesByComponentId(UUID componentId) {
        if (!gradeComponentRepository.existsById(componentId)) {
            throw new GradeComponentNotFoundException();
        }

        return gradeEntryRepository.findByComponent_Id(componentId);
    }

    @Override
    public GradeEntry getEntryById(UUID id) {
        return gradeEntryRepository.findById(id)
                .orElseThrow(GradeEntryNotFoundException::new);
    }

    @Override
    public GradeEntry updateEntry(UUID id, GradeEntry updates) {
        GradeEntry existing = getEntryById(id);

        if (updates.getTitle() != null && !updates.getTitle().isBlank()) {
            existing.setTitle(updates.getTitle());
        }
        if (updates.getScoreEarned() != null) {
            existing.setScoreEarned(updates.getScoreEarned());
        }
        if (updates.getScorePossible() != null) {
            existing.setScorePossible(updates.getScorePossible());
        }
        if (updates.getGradedOn() != null) {
            existing.setGradedOn(updates.getGradedOn());
        }

        return gradeEntryRepository.save(existing);
    }

    @Override
    public void deleteEntry(UUID id) {
        GradeEntry existing = getEntryById(id);
        gradeEntryRepository.delete(existing);
    }
}
