package com.i2p.accreditations.repository.checklist;

import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.form.Form;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {
    Optional<Checklist> findTopByOrderByNumberDesc();
    Page<Checklist> findByChapterId(UUID chapterId, Pageable pageable);

    @Query("SELECT c FROM Checklist c WHERE c.chapter.id = :chapterId " +
            "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    Page<Checklist> findByChapterIdAndKeyword(
            @Param("chapterId") UUID chapterId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(" SELECT cl FROM Checklist cl JOIN FETCH cl.chapter c JOIN FETCH c.accreditation a WHERE cl.id = :id ")
    Optional<Checklist> findByIdWithChapter(@Param("id") UUID id);
}
