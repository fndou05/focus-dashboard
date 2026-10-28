package com.frankondou.focus.repository;

import com.frankondou.focus.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    List<Subject> findBySemester_Id(UUID semesterId);

    @Query("""
        select s from Subject s
        where s.semester.user.id = :userId
          and s.semester.startDate <= :today
          and s.semester.endDate >= :today
        """)
    List<Subject> findCurrentSubjectsForUser(@Param("userId") UUID userId, @Param("today") LocalDate today);
}