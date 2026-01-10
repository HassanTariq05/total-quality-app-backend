package com.i2p.accreditations.repository.form;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
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
public interface FormRepository extends JpaRepository<Form, UUID> {
    Optional<Form> findTopByOrderByNumberDesc();
    Page<Form> findByChapterId(UUID chapterId, Pageable pageable);

    @Query("SELECT f FROM Form f WHERE f.chapter.id = :chapterId " +
            "AND (:keyword IS NULL OR LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    Page<Form> findByChapterIdAndKeyword(
            @Param("chapterId") UUID chapterId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    @Query("SELECT f FROM Form f JOIN FETCH f.chapter c JOIN FETCH c.accreditation a WHERE f.id = :id ")
    Optional<Form> findByIdWithChapter(@Param("id") UUID id);
}
