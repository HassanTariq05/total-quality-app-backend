package com.i2p.accreditations.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ChecklistSubmissionCreateDto {
    private UUID checklistId;
    private UUID organisationId;
    private String data;
    private String name;
    private String description;
}
