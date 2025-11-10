package com.i2p.accreditations.repository.formSubmission;

import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormSubmissionRepository extends JpaRepository<FormSubmission, UUID> {
    boolean existsByFormIdAndOrganisationId(UUID formId, UUID organisationId);

    Optional<FormSubmission> findByOrganisationIdAndFormId(UUID organisationId, UUID formId);
}