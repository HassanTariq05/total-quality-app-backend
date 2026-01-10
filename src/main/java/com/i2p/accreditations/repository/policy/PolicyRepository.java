package com.i2p.accreditations.repository.policy;

import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.policy.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, UUID> {
    Optional<Policy> findTopByOrderByNumberDesc();
    Page<Policy> findByChapterId(UUID chapterId, Pageable pageable);
    @Query("SELECT p FROM Policy p WHERE p.chapter.id = :chapterId " +
            "AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    Page<Policy> findByChapterIdAndKeyword(
            @Param("chapterId") UUID chapterId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(" SELECT pl FROM Policy pl JOIN FETCH pl.chapter c JOIN FETCH c.accreditation a WHERE pl.id = :id ")
    Optional<Policy> findByIdWithChapter(@Param("id") UUID id);
}
