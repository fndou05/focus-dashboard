package com.frankondou.focus.repository;

import com.frankondou.focus.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

    Optional<CheckIn> findBySession_Id(UUID sessionId);

    boolean existsBySession_Id(UUID sessionId);

    @Query("select c from CheckIn c where c.session.subject.id = :subjectId order by c.session.startedAt desc")
    List<CheckIn> findBySubjectIdNewestFirst(@Param("subjectId") UUID subjectId);
}
