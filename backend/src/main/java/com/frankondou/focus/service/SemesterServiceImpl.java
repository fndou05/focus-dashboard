package com.frankondou.focus.service;

import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.BadRequestException;
import com.frankondou.focus.exception.SemesterNotFoundException;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.SemesterRepository;
import com.frankondou.focus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final UserRepository userRepository;

    public SemesterServiceImpl(
            SemesterRepository semesterRepository,
            UserRepository userRepository) {
        this.semesterRepository = semesterRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Semester createSemester(UUID userId, Semester semester) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        validateDates(semester.getStartDate(), semester.getEndDate());

        semester.setUser(user);
        return semesterRepository.save(semester);
    }

    @Override
    public List<Semester> getSemestersByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        return semesterRepository.findByUser_Id(userId);
    }

    @Override
    public Semester getSemesterById(UUID id) {
        return semesterRepository.findById(id)
                .orElseThrow(SemesterNotFoundException::new);
    }

    @Override
    public Semester updateSemester(UUID id, Semester updates) {
        Semester existing = getSemesterById(id);

        var startDate = updates.getStartDate() != null
                ? updates.getStartDate() : existing.getStartDate();

        var endDate = updates.getEndDate() != null
                ? updates.getEndDate() : existing.getEndDate();

        validateDates(startDate, endDate);

        if (updates.getName() != null && !updates.getName().isBlank()) {
            existing.setName(updates.getName());
        }

        if (updates.getStartDate() != null) {
            existing.setStartDate(updates.getStartDate());
        }

        if (updates.getEndDate() != null) {
            existing.setEndDate(updates.getEndDate());
        }

        return semesterRepository.save(existing);
    }

    @Override
    public void deleteSemester(UUID id) {
        Semester existing = getSemesterById(id);
        semesterRepository.delete(existing);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException("Semester end date cannot be before its start date");
        }
    }
}
