package com.frankondou.focus.service;

import com.frankondou.focus.entity.GradeCutoff;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.GradeCutoffNotFoundException;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GradeCutoffServiceImpl implements GradeCutoffService {

    private final GradeCutoffRepository gradeCutoffRepository;
    private final UserRepository userRepository;

    public GradeCutoffServiceImpl(GradeCutoffRepository gradeCutoffRepository,
                                  UserRepository userRepository) {
        this.gradeCutoffRepository = gradeCutoffRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<GradeCutoff> getGradeCutoffsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        return gradeCutoffRepository.findByUser_IdOrderByMinPercentDesc(userId);
    }

    @Override
    public GradeCutoff updateGradeCutoff(UUID id, GradeCutoff updates) {
        GradeCutoff existing = gradeCutoffRepository.findById(id)
                .orElseThrow(GradeCutoffNotFoundException::new);

        if (updates.getMinPercent() != null) {
            rejectRaisingLowestCutoff(existing, updates.getMinPercent());
            existing.setMinPercent(updates.getMinPercent());
        }
        if (updates.getGradePoints() != null) {
            existing.setGradePoints(updates.getGradePoints());
        }

        return gradeCutoffRepository.save(existing);
    }

    // If the lowest cutoff rose above 0, grades below it would have no letter
    private void rejectRaisingLowestCutoff(GradeCutoff cutoff, BigDecimal newMinPercent) {
        List<GradeCutoff> cutoffs = gradeCutoffRepository
                .findByUser_IdOrderByMinPercentDesc(cutoff.getUser().getId());
        GradeCutoff lowest = cutoffs.get(cutoffs.size() - 1);

        if (lowest.getId().equals(cutoff.getId()) && newMinPercent.signum() != 0) {
            throw new BadRequestException("The lowest grade cutoff must stay at 0%");
        }
    }
}
