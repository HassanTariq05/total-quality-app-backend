package com.i2p.accreditations.service.formSubmission;

import com.i2p.accreditations.dto.FormSubmissionCreateDto;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.repository.form.FormRepository;
import com.i2p.accreditations.repository.formSubmission.FormSubmissionRepository;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
import com.i2p.accreditations.service.formIdentifier.FormIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FormSubmissionService {

    private final FormSubmissionRepository repository;
    private final OrganisationRepository organisationRepo;

    private final FormRepository formRepo;

    private final UserRepository userRepo;

    @Autowired
    private FormIdentifierService identifierService;

    public FormSubmissionService(FormSubmissionRepository repository, OrganisationRepository organisationRepository, FormRepository formRepository, UserRepository userRepository) {
        this.repository = repository;
        this.organisationRepo = organisationRepository;
        this.formRepo = formRepository;
        this.userRepo = userRepository;
    }

    public FormSubmission createFormSubmission(FormSubmissionCreateDto dto, User submittedBy) {
        Form form = formRepo.findById(dto.getFormId())
                .orElseThrow(() -> new RuntimeException("Form not found"));

        Organisation organisation = organisationRepo.findById(dto.getOrganisationId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        FormSubmission submission = new FormSubmission();
        submission.setForm(form);
        submission.setOrganisation(organisation);
        submission.setSubmittedBy(submittedBy);
        submission.setName(dto.getName());
        submission.setDescription(dto.getDescription());
        submission.setData(null);
        submission.setSubmittedAt(LocalDateTime.now());

        return repository.save(submission);
    }

    public List<FormSubmission> getByOrganisationAndForm(UUID organisationId,String keyword, UUID formId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrganisationIdAndFormId(organisationId, formId);
        }
        return repository.findAllByOrganisationIdAndFormIdAndKeyword(organisationId, formId, keyword);

    }

    public FormSubmission getBySubmissionId(UUID submissionId) {
        return repository.findById(submissionId)
                .orElse(null); // or throw a custom NotFoundException
    }

    public Optional<FormSubmission> getByOrganisationFormAndSubmission(
            UUID organisationId,
            UUID formId,
            UUID submissionId
    ) {
        return repository.findByOrganisationIdAndFormIdAndId(organisationId, formId, submissionId);
    }


    public List<FormSubmission> getAllFormSubmissions() {
        return repository.findAll();
    }

    public FormSubmission updateFormSubmission(UUID id, FormSubmission payload) {
        return repository.findById(id)
                .map(existing -> {
                    if (payload.getName() != null) existing.setName(payload.getName());
                    if (payload.getDescription() != null) existing.setDescription(payload.getDescription());
                    if (payload.getData() != null) existing.setData(payload.getData());
                    if (payload.getForm() != null) existing.setForm(payload.getForm());
                    if (payload.getOrganisation() != null) existing.setOrganisation(payload.getOrganisation());
                    if (payload.getSubmittedBy() != null) existing.setSubmittedBy(payload.getSubmittedBy());

                    existing.setSubmittedAt(LocalDateTime.now());

                    FormSubmission saved = repository.save(existing);

                    // run async identifier insertion
                    identifierService.saveIdentifiersAsync(saved);

                    return saved;
                })
                .orElseThrow(() -> new RuntimeException("FormSubmission not found with id " + id));
    }


    public void deleteFormSubmission(UUID id) {
        repository.deleteById(id);
    }
}

