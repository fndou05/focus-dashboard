package com.frankondou.focus.repository;

import com.frankondou.focus.entity.GradeComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeComponentRepository extends JpaRepository<GradeComponent, UUID> {
    List<GradeComponent> findBySubject_Id(UUID subjectId);
}