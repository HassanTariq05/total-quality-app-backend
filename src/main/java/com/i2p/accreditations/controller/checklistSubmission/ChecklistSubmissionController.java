package com.i2p.accreditations.controller.checklistSubmission;

import com.i2p.accreditations.dto.ChecklistSubmissionCreateDto;
import com.i2p.accreditations.dto.ChecklistSubmissionResponseDto;
import com.i2p.accreditations.dto.OrganisationDto;
import com.i2p.accreditations.dto.UserDto;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.checklistSubmission.ChecklistSubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/checklistSubmissions")
@ProtectedEndpoint
public class ChecklistSubmissionController {

    private final ChecklistSubmissionService service;

    private final UserRepository userRepository;

    public ChecklistSubmissionController(ChecklistSubmissionService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ChecklistSubmissionCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User submittedBy = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        // Check if a submission already exists for this checklist + organisation
        boolean exists = service.existsByChecklistAndOrganisation(dto.getChecklistId(), dto.getOrganisationId());
        if (exists) {
            return ResponseEntity.status(409).body(
                    Map.of("success", false, "message", "Submission already exists for this organisation")
            );
        }

        ChecklistSubmission submission = service.createChecklistSubmission(dto, submittedBy);
        ChecklistSubmissionResponseDto response = mapToDto(submission);
        return ResponseEntity.ok(response);
    }


    private ChecklistSubmissionResponseDto mapToDto(ChecklistSubmission submission) {
        ChecklistSubmissionResponseDto dto = new ChecklistSubmissionResponseDto();
        dto.setId(submission.getId());
        dto.setData(submission.getData());
        dto.setSubmittedAt(submission.getSubmittedAt());

        Organisation org = submission.getOrganisation();
        if (org != null) {
            OrganisationDto orgDto = new OrganisationDto();
            orgDto.setId(org.getId());
            orgDto.setName(org.getName());
            orgDto.setStatus(org.getStatus());
            dto.setOrganisation(orgDto);
        }

        User user = submission.getSubmittedBy();
        if (user != null) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            dto.setSubmittedBy(userDto);
        }

        return dto;
    }


    @GetMapping
    public ResponseEntity<List<ChecklistSubmission>> getAll() {
        return ResponseEntity.ok(service.getAllChecklistSubmissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChecklistSubmission> getById(@PathVariable UUID id) {
        return service.getChecklistSubmissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("organisationId/{organisationId}/checklistId/{checklistId}")
    public ResponseEntity<ChecklistSubmissionResponseDto> getByOrganisationAndChecklist(
            @PathVariable UUID organisationId,
            @PathVariable UUID checklistId
    ) {
        return service.getByOrganisationAndChecklist(organisationId, checklistId)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(new ChecklistSubmissionResponseDto()));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ChecklistSubmission> update(@PathVariable UUID id, @RequestBody ChecklistSubmission checklistSubmission) {
        return ResponseEntity.ok(service.updateChecklistSubmission(id, checklistSubmission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChecklistSubmission(id);
        return ResponseEntity.noContent().build();
    }
}

