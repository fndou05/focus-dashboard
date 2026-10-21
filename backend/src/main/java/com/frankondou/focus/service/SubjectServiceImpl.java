package com.frankondou.focus.service;

import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.exception.SemesterNotFoundException;
import com.frankondou.focus.exception.SubjectNotFoundException;
import com.frankondou.focus.repository.SemesterRepository;
import com.frankondou.focus.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            SemesterRepository semesterRepository) {
        this.subjectRepository = subjectRepository;
        this.semesterRepository = semesterRepository;
    }

    @Override
    public Subject createSubject(UUID semesterId, Subject subject) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(SemesterNotFoundException::new);

        subject.setSemester(semester);
        return subjectRepository.save(subject);
    }

    @Override
    public List<Subject> getSubjectsBySemesterId(UUID semesterId) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new SemesterNotFoundException();
        }

        return subjectRepository.findBySemester_Id(semesterId);
    }

    @Override
    public Subject getSubjectById(UUID id) {
        return subjectRepository.findById(id)
                .orElseThrow(SubjectNotFoundException::new);
    }

    @Override
    public Subject updateSubject(UUID id, Subject updates) {
        Subject existing = getSubjectById(id);

        if (updates.getName() != null && !updates.getName().isBlank()) {
            existing.setName(updates.getName());
        }

        if (updates.getCredits() != null) {
            existing.setCredits(updates.getCredits());
        }

        if (updates.getSelfRatedStrength() != null) {
            existing.setSelfRatedStrength(updates.getSelfRatedStrength());
        }

        return subjectRepository.save(existing);
    }

    @Override
    public void deleteSubject(UUID id) {
        Subject existing = getSubjectById(id);
        subjectRepository.delete(existing);
    }
}
