package com.i2p.accreditations.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrganisationDto {
    private UUID id;
    private String name;
    private String status;
}
