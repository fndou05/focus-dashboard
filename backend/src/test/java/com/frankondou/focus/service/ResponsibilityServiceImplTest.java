package com.frankondou.focus.service;

import com.frankondou.focus.entity.Responsibility;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.entity.enums.ResponsibilityCategory;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.ResponsibilityRepository;
import com.frankondou.focus.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResponsibilityServiceImplTest {

    private final ResponsibilityRepository responsibilityRepository = mock(ResponsibilityRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ResponsibilityServiceImpl service =
            new ResponsibilityServiceImpl(responsibilityRepository, userRepository);

    @Test
    void createResponsibility_forUnknownUser_throwsNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Responsibility responsibility = new Responsibility();

        assertThrows(UserNotFoundException.class, () -> service.createResponsibility(userId, responsibility));
        verify(responsibilityRepository, never()).save(any());
    }

    @Test
    void createResponsibility_forKnownUser_savesWithUserAttached() {
        UUID userId = UUID.randomUUID();
        User user = new User("a@b.com", "Franko", "America/New_York");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(responsibilityRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        Responsibility saved = service.createResponsibility(userId, new Responsibility());

        assertSame(user, saved.getUser());
    }

    @Test
    void updateResponsibility_withOnlyHours_leavesLabelAndCategoryAlone() {
        UUID id = UUID.randomUUID();
        Responsibility existing = new Responsibility(null, "Gym", ResponsibilityCategory.CUSTOM, new BigDecimal("5"));
        when(responsibilityRepository.findById(id)).thenReturn(Optional.of(existing));
        when(responsibilityRepository.save(any())).thenAnswer(call -> call.getArgument(0));

        Responsibility updates = new Responsibility();
        updates.setHoursPerWeek(new BigDecimal("8"));

        Responsibility result = service.updateResponsibility(id, updates);

        assertEquals(new BigDecimal("8"), result.getHoursPerWeek());
        assertEquals("Gym", result.getLabel());
        assertEquals(ResponsibilityCategory.CUSTOM, result.getCategory());
    }
}
