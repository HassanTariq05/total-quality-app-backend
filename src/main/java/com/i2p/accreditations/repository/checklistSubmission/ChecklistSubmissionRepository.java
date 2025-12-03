package com.i2p.accreditations.repository.checklistSubmission;

import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistSubmissionRepository extends JpaRepository<ChecklistSubmission, UUID> {
    boolean existsByChecklistIdAndOrganisationId(UUID checklistId, UUID organisationId);

    List<ChecklistSubmission> findAllByOrganisationIdAndChecklistId(UUID organisationId, UUID checklistId);

    Optional<ChecklistSubmission> findByOrganisationIdAndChecklistIdAndId(
            UUID organisationId,
            UUID checklistId,
            UUID submissionId
    );

    Optional<ChecklistSubmission> findById(UUID submissionId);
}