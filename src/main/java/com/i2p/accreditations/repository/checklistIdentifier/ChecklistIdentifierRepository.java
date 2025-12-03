package com.i2p.accreditations.repository.checklistIdentifier;

import com.i2p.accreditations.model.checklistIdentifier.ChecklistIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ChecklistIdentifierRepository extends JpaRepository<ChecklistIdentifier, UUID> {
    @Transactional
    @Modifying
    @Query("DELETE FROM ChecklistIdentifier c WHERE c.submission.id = :submissionId")
    void deleteBySubmissionId(@Param("submissionId") UUID submissionId);
}
