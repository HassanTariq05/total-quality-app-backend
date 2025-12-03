package com.i2p.accreditations.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GetFormSubmissionDto {
    private UUID id;
    private String name;
    private String description;
    private String data;
    private LocalDateTime submittedAt;
    private OrganisationDto organisation;
    private UserDto submittedBy;
    private UUID formId;
}

