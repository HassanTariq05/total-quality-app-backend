package com.i2p.accreditations.repository.formIdentifier;

import com.i2p.accreditations.model.formIdentifier.FormIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface FormIdentifierRepository extends JpaRepository<FormIdentifier, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM FormIdentifier f WHERE f.submission.id = :submissionId")
    void deleteBySubmissionId(@Param("submissionId") UUID submissionId);

}
