package com.frankondou.focus.repository;

import com.frankondou.focus.entity.WeeklyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WeeklyGoalRepository extends JpaRepository<WeeklyGoal, UUID> {
    List<WeeklyGoal> findBySubject_Id(UUID subjectId);
}