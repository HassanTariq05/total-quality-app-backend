package com.i2p.accreditations.repository.formSubmission;

import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormSubmissionRepository extends JpaRepository<FormSubmission, UUID> {

    boolean existsByFormIdAndOrganisationId(UUID formId, UUID organisationId);

    List<FormSubmission> findAllByOrganisationIdAndFormId(UUID organisationId, UUID formId);

    @Query("SELECT fs FROM FormSubmission fs " +
            "WHERE fs.organisation.id = :organisationId " +
            "AND fs.form.id = :formId " +
            "AND LOWER(fs.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FormSubmission> findAllByOrganisationIdAndFormIdAndKeyword(
            @Param("organisationId") UUID organisationId,
            @Param("formId") UUID formId,
            @Param("keyword") String keyword
    );

    Optional<FormSubmission> findByOrganisationIdAndFormIdAndId(
            UUID organisationId,
            UUID formId,
            UUID submissionId
    );

    Optional<FormSubmission> findById(UUID submissionId);
}
