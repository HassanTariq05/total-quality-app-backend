package com.i2p.accreditations.controller.checklistSubmission;

import com.i2p.accreditations.dto.*;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.checklistSubmission.ChecklistSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('PERMISSION_CREATE_CHECKLIST_SUBMISSION')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ChecklistSubmissionCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User submittedBy = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

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


    @PreAuthorize("hasAuthority('PERMISSION_VIEW_CHECKLIST_SUBMISSION')")
    @GetMapping
    public ResponseEntity<List<ChecklistSubmission>> getAll() {
        return ResponseEntity.ok(service.getAllChecklistSubmissions());
    }

    @PreAuthorize("hasAuthority('PERMISSION_VIEW_CHECKLIST_SUBMISSION')")
    @GetMapping("/getBySubmissionId/{submissionId}")
    public ResponseEntity<?> getById(@PathVariable UUID submissionId) {
        try {
            ChecklistSubmission submission = service.getBySubmissionId(submissionId);

            if (submission == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("ChecklistSubmission not found for id: " + submissionId);
            }

            GetChecklistSubmissionDto dto = mapToSingleDto(submission);

            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }

    @PreAuthorize("hasAuthority('PERMISSION_VIEW_CHECKLIST_SUBMISSION')")
    @GetMapping("organisationId/{organisationId}/checklistId/{checklistId}")
    public ResponseEntity<List<ChecklistSubmissionListDto>> getByOrganisationAndChecklist(
            @PathVariable UUID organisationId,
            @PathVariable UUID checklistId
    ) {
        List<ChecklistSubmission> submissions = service.getByOrganisationAndChecklist(organisationId, checklistId);

        if (submissions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<ChecklistSubmissionListDto> dtoList = submissions.stream()
                .map(this::mapToListDto)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    private ChecklistSubmissionListDto mapToListDto(ChecklistSubmission submission) {
        ChecklistSubmissionListDto dto = new ChecklistSubmissionListDto();
        dto.setId(submission.getId());
        dto.setName(submission.getName());
        dto.setDescription(submission.getDescription());
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

    private GetChecklistSubmissionDto mapToSingleDto(ChecklistSubmission submission) {
        GetChecklistSubmissionDto dto = new GetChecklistSubmissionDto();
        dto.setId(submission.getId());
        dto.setName(submission.getName());
        dto.setData(submission.getData());
        dto.setDescription(submission.getDescription());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setChecklistId(submission.getChecklist().getId());

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


    @PreAuthorize("hasAuthority('PERMISSION_VIEW_CHECKLIST_SUBMISSION')")
    @GetMapping("organisationId/{organisationId}/checklistId/{checklistId}/submissionId/{submissionId}")
    public ResponseEntity<ChecklistSubmissionResponseDto> getByOrganisationChecklistAndSubmission(
            @PathVariable UUID organisationId,
            @PathVariable UUID checklistId,
            @PathVariable UUID submissionId
    ) {
        return service.getByOrganisationChecklistAndSubmission(organisationId, checklistId, submissionId)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('PERMISSION_EDIT_CHECKLIST_SUBMISSION')")
    @PutMapping("/{id}")
    public ResponseEntity<ChecklistSubmission> update(@PathVariable UUID id, @RequestBody ChecklistSubmission checklistSubmission) {
        return ResponseEntity.ok(service.updateChecklistSubmission(id, checklistSubmission));
    }

    @PreAuthorize("hasAuthority('PERMISSION_DELETE_CHECKLIST_SUBMISSION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteChecklistSubmission(id);
        return ResponseEntity.noContent().build();
    }
}

