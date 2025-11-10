package com.i2p.accreditations.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class FormSubmissionResponseDto {
    private UUID id;
    private String data;
    private LocalDateTime submittedAt;
    private UserDto submittedBy;
    private OrganisationDto organisation;
    private FormDto form;
}
