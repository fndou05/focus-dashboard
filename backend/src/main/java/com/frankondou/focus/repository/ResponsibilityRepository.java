package com.frankondou.focus.repository;

import com.frankondou.focus.entity.Responsibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResponsibilityRepository extends JpaRepository<Responsibility, UUID> {
    List<Responsibility> findByUser_Id(UUID userId);
}
