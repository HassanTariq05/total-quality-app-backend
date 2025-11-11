package com.i2p.accreditations.service.checklistSubmission;

import com.i2p.accreditations.dto.ChecklistSubmissionCreateDto;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.repository.checklist.ChecklistRepository;
import com.i2p.accreditations.repository.checklistSubmission.ChecklistSubmissionRepository;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
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
        submission.setData(dto.getData());
        submission.setSubmittedAt(LocalDateTime.now());

        return repository.save(submission);
    }

    public boolean existsByChecklistAndOrganisation(UUID checklistId, UUID organisationId) {
        return repository.existsByChecklistIdAndOrganisationId(checklistId, organisationId);
    }

    public Optional<ChecklistSubmission> getByOrganisationAndChecklist(UUID organisationId, UUID checklistId) {
        return repository.findByOrganisationIdAndChecklistId(organisationId, checklistId);
    }

    public List<ChecklistSubmission> getAllChecklistSubmissions() {
        return repository.findAll();
    }

    public Optional<ChecklistSubmission> getChecklistSubmissionById(UUID id) {
        return repository.findById(id);
    }

    public ChecklistSubmission updateChecklistSubmission(UUID id, ChecklistSubmission checklistSubmissionDetails) {
        return repository.findById(id).map(existing -> {
            existing.setData(checklistSubmissionDetails.getData());
            existing.setSubmittedAt(LocalDateTime.now());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("ChecklistSubmission not found with id " + id));
    }


    public void deleteChecklistSubmission(UUID id) {
        repository.deleteById(id);
    }
}

