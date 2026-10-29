package com.frankondou.focus.service;

import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.ResponsibilityNotFoundException;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.ResponsibilityRepository;
import com.frankondou.focus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ResponsibilityServiceImpl implements ResponsibilityService {

    private final ResponsibilityRepository responsibilityRepository;
    private final UserRepository userRepository;

    public ResponsibilityServiceImpl(ResponsibilityRepository responsibilityRepository,
                                     UserRepository userRepository) {
        this.responsibilityRepository = responsibilityRepository;
        this.userRepository = userRepository;
    }

    // No cap on total hours: the free-time calculation warns about an overbooked week instead
    @Override
    public Responsibility createResponsibility(UUID userId, Responsibility responsibility) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        responsibility.setUser(user);
        return responsibilityRepository.save(responsibility);
    }

    @Override
    public List<Responsibility> getResponsibilitiesByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        return responsibilityRepository.findByUser_Id(userId);
    }

    @Override
    public Responsibility getResponsibilityById(UUID id) {
        return responsibilityRepository.findById(id)
                .orElseThrow(ResponsibilityNotFoundException::new);
    }

    @Override
    public Responsibility updateResponsibility(UUID id, Responsibility updates) {
        Responsibility existing = getResponsibilityById(id);

        if (updates.getLabel() != null && !updates.getLabel().isBlank()) {
            existing.setLabel(updates.getLabel());
        }
        if (updates.getCategory() != null) {
            existing.setCategory(updates.getCategory());
        }
        if (updates.getHoursPerWeek() != null) {
            existing.setHoursPerWeek(updates.getHoursPerWeek());
        }

        return responsibilityRepository.save(existing);
    }

    @Override
    public void deleteResponsibility(UUID id) {
        Responsibility existing = getResponsibilityById(id);
        responsibilityRepository.delete(existing);
    }
}
