package com.i2p.accreditations.service.checklistSubmission;

import com.i2p.accreditations.dto.ChecklistSubmissionCreateDto;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.repository.checklist.ChecklistRepository;
import com.i2p.accreditations.repository.checklistSubmission.ChecklistSubmissionRepository;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
import com.i2p.accreditations.service.checklistIdentifier.ChecklistIdentifierService;
import com.i2p.accreditations.service.formIdentifier.FormIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChecklistSubmissionService {

    private final ChecklistSubmissionRepository repository;
    private final OrganisationRepository organisationRepo;

    private final ChecklistRepository checklistRepo;

    private final UserRepository userRepo;

    @Autowired
    private ChecklistIdentifierService identifierService;

    public ChecklistSubmissionService(ChecklistSubmissionRepository repository, OrganisationRepository organisationRepository, ChecklistRepository checklistRepository, UserRepository userRepository) {
        this.repository = repository;
        this.organisationRepo = organisationRepository;
        this.checklistRepo = checklistRepository;
        this.userRepo = userRepository;
    }

    public ChecklistSubmission createChecklistSubmission(ChecklistSubmissionCreateDto dto, User submittedBy) {
        Checklist checklist = checklistRepo.findById(dto.getChecklistId())
                .orElseThrow(() -> new RuntimeException("Checklist not found"));

        Organisation organisation = organisationRepo.findById(dto.getOrganisationId())
                .orElseThrow(() -> new RuntimeException("Organisation not found"));

        ChecklistSubmission submission = new ChecklistSubmission();
        submission.setChecklist(checklist);
        submission.setOrganisation(organisation);
        submission.setSubmittedBy(submittedBy);
        submission.setName(dto.getName());
        submission.setDescription(dto.getDescription());
        submission.setData(null);
        submission.setSubmittedAt(LocalDateTime.now());

        return repository.save(submission);
    }

    public boolean existsByChecklistAndOrganisation(UUID checklistId, UUID organisationId) {
        return repository.existsByChecklistIdAndOrganisationId(checklistId, organisationId);
    }

    public List<ChecklistSubmission> getByOrganisationAndChecklist(UUID organisationId, UUID checklistId) {
        return repository.findAllByOrganisationIdAndChecklistId(organisationId, checklistId);
    }

    public ChecklistSubmission getBySubmissionId(UUID submissionId) {
        return repository.findById(submissionId)
                .orElse(null);
    }

    public Optional<ChecklistSubmission> getByOrganisationChecklistAndSubmission(
            UUID organisationId,
            UUID checklistId,
            UUID submissionId
    ) {
        return repository.findByOrganisationIdAndChecklistIdAndId(organisationId, checklistId, submissionId);
    }


    public List<ChecklistSubmission> getAllChecklistSubmissions() {
        return repository.findAll();
    }

    public ChecklistSubmission updateChecklistSubmission(UUID id, ChecklistSubmission payload) {
        return repository.findById(id)
                .map(existing -> {
                    if (payload.getName() != null) existing.setName(payload.getName());
                    if (payload.getDescription() != null) existing.setDescription(payload.getDescription());
                    if (payload.getData() != null) existing.setData(payload.getData());
                    if (payload.getChecklist() != null) existing.setChecklist(payload.getChecklist());
                    if (payload.getOrganisation() != null) existing.setOrganisation(payload.getOrganisation());
                    if (payload.getSubmittedBy() != null) existing.setSubmittedBy(payload.getSubmittedBy());

                    existing.setSubmittedAt(LocalDateTime.now());

                    ChecklistSubmission saved = repository.save(existing);

                    identifierService.saveIdentifiersAsync(saved);

                    return saved;
                })
                .orElseThrow(() -> new RuntimeException("ChecklistSubmission not found with id " + id));
    }


    public void deleteChecklistSubmission(UUID id) {
        repository.deleteById(id);
    }
}

