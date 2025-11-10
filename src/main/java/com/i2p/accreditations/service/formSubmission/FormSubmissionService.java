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
        submission.setData(dto.getData());
        submission.setSubmittedAt(LocalDateTime.now());

        return repository.save(submission);
    }

    public boolean existsByFormAndOrganisation(UUID formId, UUID organisationId) {
        return repository.existsByFormIdAndOrganisationId(formId, organisationId);
    }

    public Optional<FormSubmission> getByOrganisationAndForm(UUID organisationId, UUID formId) {
        return repository.findByOrganisationIdAndFormId(organisationId, formId);
    }

    public List<FormSubmission> getAllFormSubmissions() {
        return repository.findAll();
    }

    public Optional<FormSubmission> getFormSubmissionById(UUID id) {
        return repository.findById(id);
    }

    public FormSubmission updateFormSubmission(UUID id, FormSubmission formSubmissionDetails) {
        return repository.findById(id).map(existing -> {
            existing.setData(formSubmissionDetails.getData());
            existing.setSubmittedAt(LocalDateTime.now());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("FormSubmission not found with id " + id));
    }


    public void deleteFormSubmission(UUID id) {
        repository.deleteById(id);
    }
}

