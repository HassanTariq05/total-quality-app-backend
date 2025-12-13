package com.i2p.accreditations.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PolicyVersionDto {
    private UUID id;
    private Long number;
    private String title;
    private String description;
    private String status;
    private UUID policyId;
    private String documentName;
}