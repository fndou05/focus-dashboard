package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.GradeComponentNotFoundException;
import com.frankondou.focus.exception.SubjectNotFoundException;
import com.frankondou.focus.repository.GradeComponentRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GradeComponentServiceImpl implements GradeComponentService {

    private static final BigDecimal MAX_TOTAL_WEIGHT = new BigDecimal("100");

    private final GradeComponentRepository gradeComponentRepository;
    private final SubjectRepository subjectRepository;

    public GradeComponentServiceImpl(GradeComponentRepository gradeComponentRepository,
                                     SubjectRepository subjectRepository) {
        this.gradeComponentRepository = gradeComponentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public GradeComponent createGradeComponent(UUID subjectId, GradeComponent component) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(SubjectNotFoundException::new);

        validateTotalWeight(subjectId, null, component.getWeightPercent());

        component.setSubject(subject);
        return gradeComponentRepository.save(component);
    }

    @Override
    public List<GradeComponent> getGradeComponentsBySubjectId(UUID subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new SubjectNotFoundException();
        }

        return gradeComponentRepository.findBySubject_Id(subjectId);
    }

    @Override
    public GradeComponent getGradeComponentById(UUID id) {
        return gradeComponentRepository.findById(id)
                .orElseThrow(GradeComponentNotFoundException::new);
    }

    @Override
    public GradeComponent updateGradeComponent(UUID id, GradeComponent updates) {
        GradeComponent existing = getGradeComponentById(id);

        if (updates.getName() != null && !updates.getName().isBlank()) {
            existing.setName(updates.getName());
        }
        if (updates.getCategory() != null) {
            existing.setCategory(updates.getCategory());
        }
        if (updates.getWeightPercent() != null) {
            validateTotalWeight(existing.getSubject().getId(), existing.getId(), updates.getWeightPercent());
            existing.setWeightPercent(updates.getWeightPercent());
        }

        return gradeComponentRepository.save(existing);
    }

    @Override
    public void deleteGradeComponent(UUID id) {
        GradeComponent existing = getGradeComponentById(id);
        gradeComponentRepository.delete(existing);
    }

    // On update, skip the component being edited so its old weight isn't counted twice
    private void validateTotalWeight(UUID subjectId, UUID excludedComponentId, BigDecimal newWeight) {
        BigDecimal total = gradeComponentRepository.findBySubject_Id(subjectId).stream()
                .filter(c -> !c.getId().equals(excludedComponentId))
                .map(GradeComponent::getWeightPercent)
                .reduce(newWeight, BigDecimal::add);

        if (total.compareTo(MAX_TOTAL_WEIGHT) > 0) {
            throw new BadRequestException("Total weight of a subject's components cannot exceed 100%");
        }
    }
}
