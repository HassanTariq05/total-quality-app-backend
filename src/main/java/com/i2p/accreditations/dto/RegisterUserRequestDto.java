package com.i2p.accreditations.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class RegisterUserRequestDto {
    private String name;
    private String email;
    private String password;
    private UUID organisationId;
}
