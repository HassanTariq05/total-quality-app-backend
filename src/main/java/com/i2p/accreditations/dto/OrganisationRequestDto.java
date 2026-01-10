package com.i2p.accreditations.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class OrganisationRequestDto {

    private String name;
    private String email;
    private String phoneNumber;
    private String description;
    private String status;
    private Set<UUID> accreditationIds = new HashSet<>();
}
