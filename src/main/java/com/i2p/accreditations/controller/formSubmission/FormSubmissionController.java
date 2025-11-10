package com.i2p.accreditations.controller.formSubmission;

import com.i2p.accreditations.dto.FormSubmissionCreateDto;
import com.i2p.accreditations.dto.FormSubmissionResponseDto;
import com.i2p.accreditations.dto.OrganisationDto;
import com.i2p.accreditations.dto.UserDto;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.repository.access.UserRepository;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.formSubmission.FormSubmissionService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody FormSubmissionCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User submittedBy = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        // Check if a submission already exists for this form + organisation
        boolean exists = service.existsByFormAndOrganisation(dto.getFormId(), dto.getOrganisationId());
        if (exists) {
            return ResponseEntity.status(409).body(
                    Map.of("success", false, "message", "Submission already exists for this organisation")
            );
        }

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


    @GetMapping
    public ResponseEntity<List<FormSubmission>> getAll() {
        return ResponseEntity.ok(service.getAllFormSubmissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormSubmission> getById(@PathVariable UUID id) {
        return service.getFormSubmissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("organisationId/{organisationId}/formId/{formId}")
    public ResponseEntity<FormSubmissionResponseDto> getByOrganisationAndForm(
            @PathVariable UUID organisationId,
            @PathVariable UUID formId
    ) {
        return service.getByOrganisationAndForm(organisationId, formId)
                .map(this::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<FormSubmission> update(@PathVariable UUID id, @RequestBody FormSubmission formSubmission) {
        return ResponseEntity.ok(service.updateFormSubmission(id, formSubmission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteFormSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
