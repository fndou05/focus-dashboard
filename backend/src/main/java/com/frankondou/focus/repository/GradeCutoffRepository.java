package com.frankondou.focus.repository;

import com.frankondou.focus.entity.GradeCutoff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeCutoffRepository extends JpaRepository<GradeCutoff, UUID> {
    List<GradeCutoff> findByUser_IdOrderByMinPercentDesc(UUID userId);

    boolean existsByUser_IdAndLetter(UUID userId, String letter);

    Optional<GradeCutoff> findByUser_IdAndLetter(UUID userId, String letter);
}