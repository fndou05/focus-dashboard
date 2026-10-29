package com.frankondou.focus.repository;

import com.frankondou.focus.entity.GradeEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeEntryRepository extends JpaRepository<GradeEntry, UUID> {
    List<GradeEntry> findByComponent_Id(UUID componentId);
}