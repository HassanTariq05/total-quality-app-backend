package com.i2p.accreditations.repository.checklistSubmission;

import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistSubmissionRepository extends JpaRepository<ChecklistSubmission, UUID> {
    boolean existsByChecklistIdAndOrganisationId(UUID checklistId, UUID organisationId);

    Optional<ChecklistSubmission> findByOrganisationIdAndChecklistId(UUID organisationId, UUID checklistId);
}