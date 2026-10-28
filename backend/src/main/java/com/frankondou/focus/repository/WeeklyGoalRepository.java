package com.frankondou.focus.repository;

import com.frankondou.focus.entity.WeeklyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WeeklyGoalRepository extends JpaRepository<WeeklyGoal, UUID> {
    List<WeeklyGoal> findBySubject_Id(UUID subjectId);

    Optional<WeeklyGoal> findFirstBySubject_IdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(UUID subjectId, LocalDate date);
}