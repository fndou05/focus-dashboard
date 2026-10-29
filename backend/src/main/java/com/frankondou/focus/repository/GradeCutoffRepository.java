package com.frankondou.focus.repository;

import com.frankondou.focus.entity.GradeCutoff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeCutoffRepository extends JpaRepository<GradeCutoff, UUID> {
    List<GradeCutoff> findByUser_IdOrderByMinPercentDesc(UUID userId);
}