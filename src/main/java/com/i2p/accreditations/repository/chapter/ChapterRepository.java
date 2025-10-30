package com.i2p.accreditations.repository.chapter;

import com.i2p.accreditations.model.chapter.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {
    Optional<Chapter> findTopByOrderByNumberDesc();
    List<Chapter> findByAccreditationId(UUID accreditationId);

    @Query("SELECT c FROM Chapter c JOIN FETCH c.accreditation WHERE c.id = :id")
    Optional<Chapter> findByIdWithAccreditation(@Param("id") UUID id);
}
