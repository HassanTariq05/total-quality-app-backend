package com.i2p.accreditations.dto;

import com.i2p.accreditations.enums.Permission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.model.role.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RoleDto {

    private UUID id;

    private String name;

    private String description;

    private UUID organisationId;

    private Set<String> permissions;

    public RoleDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();

        this.organisationId = Optional.ofNullable(role.getOrganisation())
                .map(Organisation::getId)
                .orElse(null);

        this.permissions = role.getPermissions()
                .stream()
                .map(Permission::name)
                .collect(Collectors.toSet());
    }
}