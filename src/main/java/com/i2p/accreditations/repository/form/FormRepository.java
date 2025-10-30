package com.i2p.accreditations.repository.form;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
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
    List<Form> findByChapterId(UUID chapterId);
    @Query("""
    SELECT f FROM Form f
    JOIN FETCH f.chapter c
    JOIN FETCH c.accreditation a
    WHERE f.id = :id
    """)
    Optional<Form> findByIdWithChapter(@Param("id") UUID id);
}
