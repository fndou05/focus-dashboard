package com.frankondou.focus.service;

import com.frankondou.focus.dto.ComponentGradeResponse;
import com.frankondou.focus.dto.CumulativeGpaResponse;
import com.frankondou.focus.dto.SemesterGpaResponse;
import com.frankondou.focus.dto.SemesterGpaSummary;
import com.frankondou.focus.dto.SubjectGradeResponse;
import com.frankondou.focus.entity.GradeComponent;
import com.frankondou.focus.entity.GradeCutoff;
import com.frankondou.focus.entity.GradeEntry;
import com.frankondou.focus.entity.Semester;
import com.frankondou.focus.entity.Subject;
import com.frankondou.focus.entity.User;
import com.frankondou.focus.exception.SemesterNotFoundException;
import com.frankondou.focus.exception.SubjectNotFoundException;
import com.frankondou.focus.exception.UserNotFoundException;
import com.frankondou.focus.repository.GradeComponentRepository;
import com.frankondou.focus.repository.GradeCutoffRepository;
import com.frankondou.focus.repository.GradeEntryRepository;
import com.frankondou.focus.repository.SemesterRepository;
import com.frankondou.focus.repository.SubjectRepository;
import com.frankondou.focus.repository.UserRepository;
import com.frankondou.focus.service.GradeCalculator.CreditPoints;
import com.frankondou.focus.service.GradeCalculator.WeightedPercent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GradeServiceImpl implements GradeService {

    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final UserRepository userRepository;
    private final GradeComponentRepository gradeComponentRepository;
    private final GradeEntryRepository gradeEntryRepository;
    private final GradeCutoffRepository gradeCutoffRepository;

    public GradeServiceImpl(SubjectRepository subjectRepository,
                            SemesterRepository semesterRepository,
                            UserRepository userRepository,
                            GradeComponentRepository gradeComponentRepository,
                            GradeEntryRepository gradeEntryRepository,
                            GradeCutoffRepository gradeCutoffRepository) {
        this.subjectRepository = subjectRepository;
        this.semesterRepository = semesterRepository;
        this.userRepository = userRepository;
        this.gradeComponentRepository = gradeComponentRepository;
        this.gradeEntryRepository = gradeEntryRepository;
        this.gradeCutoffRepository = gradeCutoffRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectGradeResponse getSubjectGrade(UUID subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(SubjectNotFoundException::new);

        UUID userId = subject.getSemester().getUser().getId();
        List<GradeCutoff> cutoffs = gradeCutoffRepository.findByUser_IdOrderByMinPercentDesc(userId);

        return buildSubjectGrade(subject, userId, cutoffs);
    }

    @Override
    @Transactional(readOnly = true)
    public SemesterGpaResponse getSemesterGpa(UUID semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(SemesterNotFoundException::new);

        UUID userId = semester.getUser().getId();
        List<GradeCutoff> cutoffs = gradeCutoffRepository.findByUser_IdOrderByMinPercentDesc(userId);

        return buildSemesterGpa(semester, userId, cutoffs);
    }

    @Override
    @Transactional(readOnly = true)
    public CumulativeGpaResponse getCumulativeGpa(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<GradeCutoff> cutoffs = gradeCutoffRepository.findByUser_IdOrderByMinPercentDesc(userId);

        List<SemesterGpaSummary> summaries = new ArrayList<>();
        List<CreditPoints> allGraded = new ArrayList<>();
        for (Semester semester : semesterRepository.findByUser_Id(userId)) {
            SemesterGpaResponse semesterGpa = buildSemesterGpa(semester, userId, cutoffs);
            summaries.add(new SemesterGpaSummary(semesterGpa.semesterId(), semesterGpa.semesterName(),
                    semesterGpa.gpa(), semesterGpa.gradedCredits()));
            allGraded.addAll(gradedCreditPoints(semesterGpa.subjects()));
        }

        BigDecimal trackedCredits = totalCredits(allGraded);
        BigDecimal cumulativeGpa = GradeCalculator.cumulativeGpa(user.getPriorCredits(), user.getPriorGpa(), allGraded);

        return new CumulativeGpaResponse(userId, user.getPriorCredits(), user.getPriorGpa(),
                trackedCredits, cumulativeGpa, summaries);
    }

    private SemesterGpaResponse buildSemesterGpa(Semester semester, UUID userId, List<GradeCutoff> cutoffs) {
        List<SubjectGradeResponse> subjects = subjectRepository.findBySemester_Id(semester.getId()).stream()
                .map(subject -> buildSubjectGrade(subject, userId, cutoffs))
                .toList();

        List<CreditPoints> graded = gradedCreditPoints(subjects);
        return new SemesterGpaResponse(semester.getId(), semester.getName(),
                GradeCalculator.gpa(graded), totalCredits(graded), subjects);
    }

    private SubjectGradeResponse buildSubjectGrade(Subject subject, UUID userId, List<GradeCutoff> cutoffs) {
        List<ComponentGradeResponse> components = new ArrayList<>();
        List<WeightedPercent> parts = new ArrayList<>();
        for (GradeComponent component : gradeComponentRepository.findBySubject_Id(subject.getId())) {
            List<GradeEntry> entries = gradeEntryRepository.findByComponent_Id(component.getId());
            BigDecimal percent = GradeCalculator.componentPercent(entries);

            components.add(new ComponentGradeResponse(component.getId(), component.getName(),
                    component.getCategory(), component.getWeightPercent(), percent, entries.size()));
            parts.add(new WeightedPercent(component.getWeightPercent(), percent));
        }

        // Still calculated when a final letter exists, so the student can compare the two
        BigDecimal currentPercent = GradeCalculator.weightedPercent(parts);

        Optional<GradeCutoff> finalCutoff = subject.getFinalLetterGrade() == null
                ? Optional.empty()
                : gradeCutoffRepository.findByUser_IdAndLetter(userId, subject.getFinalLetterGrade());

        boolean isFinal = finalCutoff.isPresent();
        Optional<GradeCutoff> cutoff = isFinal ? finalCutoff : GradeCalculator.letterFor(currentPercent, cutoffs);

        return new SubjectGradeResponse(subject.getId(), subject.getName(), subject.getCredits(),
                currentPercent,
                cutoff.map(GradeCutoff::getLetter).orElse(null),
                cutoff.map(GradeCutoff::getGradePoints).orElse(null),
                isFinal, components);
    }

    private List<CreditPoints> gradedCreditPoints(List<SubjectGradeResponse> subjects) {
        return subjects.stream()
                .filter(s -> s.gradePoints() != null)
                .map(s -> new CreditPoints(s.credits(), s.gradePoints()))
                .toList();
    }

    private BigDecimal totalCredits(List<CreditPoints> graded) {
        return graded.stream().map(CreditPoints::credits).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
