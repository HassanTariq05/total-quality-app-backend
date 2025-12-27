package com.i2p.accreditations.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class UpdateUserRequestDto {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String status;
    private UUID organizationId;
    private UUID roleId;
}
