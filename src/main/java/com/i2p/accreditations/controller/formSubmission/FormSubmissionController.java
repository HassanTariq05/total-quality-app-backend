package com.i2p.accreditations.controller.formSubmission;

import com.i2p.accreditations.dto.*;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.formSubmission.FormSubmissionService;
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
@RequestMapping("/api/formSubmissions")
@ProtectedEndpoint
public class FormSubmissionController {

    private final FormSubmissionService service;

    private final UserRepository userRepository;

    public FormSubmissionController(FormSubmissionService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('PERMISSION_CREATE_FORM_SUBMISSION')")
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody FormSubmissionCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User submittedBy = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        FormSubmission submission = service.createFormSubmission(dto, submittedBy);
        FormSubmissionResponseDto response = mapToDto(submission);
        return ResponseEntity.ok(response);
    }



    private FormSubmissionResponseDto mapToDto(FormSubmission submission) {
        FormSubmissionResponseDto dto = new FormSubmissionResponseDto();
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


    @PreAuthorize("hasAuthority('PERMISSION_VIEW_FORM_SUBMISSION')")
    @GetMapping
    public ResponseEntity<List<FormSubmission>> getAll() {
        return ResponseEntity.ok(service.getAllFormSubmissions());
    }


    @PreAuthorize("hasAuthority('PERMISSION_VIEW_FORM_SUBMISSION')")
    @GetMapping("/getBySubmissionId/{submissionId}")
    public ResponseEntity<?> getById(@PathVariable UUID submissionId) {
        try {
            FormSubmission submission = service.getBySubmissionId(submissionId);

            if (submission == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("FormSubmission not found for id: " + submissionId);
            }

            GetFormSubmissionDto dto = mapToSingleDto(submission);

            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }



    @PreAuthorize("hasAuthority('PERMISSION_VIEW_FORM_SUBMISSION')")
    @GetMapping("organisationId/{organisationId}/formId/{formId}")
    public ResponseEntity<List<FormSubmissionListDto>> getByOrganisationAndForm(
            @PathVariable UUID organisationId,
            @PathVariable UUID formId
    ) {
        List<FormSubmission> submissions = service.getByOrganisationAndForm(organisationId, formId);

        if (submissions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<FormSubmissionListDto> dtoList = submissions.stream()
                .map(this::mapToListDto)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    private FormSubmissionListDto mapToListDto(FormSubmission submission) {
        FormSubmissionListDto dto = new FormSubmissionListDto();
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

    private GetFormSubmissionDto mapToSingleDto(FormSubmission submission) {
        GetFormSubmissionDto dto = new GetFormSubmissionDto();
        dto.setId(submission.getId());
        dto.setName(submission.getName());
        dto.setData(submission.getData());
        dto.setDescription(submission.getDescription());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setFormId(submission.getForm().getId());

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


    @PreAuthorize("hasAuthority('PERMISSION_VIEW_FORM_SUBMISSION')")
    @GetMapping("organisationId/{organisationId}/formId/{formId}/submissionId/{submissionId}")
    public ResponseEntity<FormSubmissionResponseDto> getByOrganisationFormAndSubmission(
            @PathVariable UUID organisationId,
            @PathVariable UUID formId,
            @PathVariable UUID submissionId
    ) {
        return service.getByOrganisationFormAndSubmission(organisationId, formId, submissionId)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('PERMISSION_EDIT_FORM_SUBMISSION')")
    @PutMapping("/{id}")
    public ResponseEntity<FormSubmission> update(
            @PathVariable UUID id,
            @RequestBody FormSubmission formSubmission
    ) {
        return ResponseEntity.ok(service.updateFormSubmission(id, formSubmission));
    }


    @PreAuthorize("hasAuthority('PERMISSION_DELETE_FORM_SUBMISSION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteFormSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
