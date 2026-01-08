package com.i2p.accreditations.service.role;

import com.i2p.accreditations.dto.RoleDto;
import com.i2p.accreditations.enums.Permission;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.model.role.Role;
import com.i2p.accreditations.repository.organisation.OrganisationRepository;
import com.i2p.accreditations.repository.role.RoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final OrganisationRepository organisationRepository;

    public RoleService(RoleRepository roleRepository, OrganisationRepository organisationRepository) {
        this.roleRepository = roleRepository;
        this.organisationRepository = organisationRepository;
    }

    private static final Set<String> RESERVED_ROLE_NAMES = Set.of(
            "Super Admin",
            "Administrator"
    );

    public Role createRole(RoleDto roleDto) {
        String roleName = roleDto.getName().trim();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_Super Admin"));

        if (!isSuperAdmin && RESERVED_ROLE_NAMES.contains(roleName)) {
            throw new IllegalArgumentException(
                    "Role name '" + roleName + "' is reserved and can only be managed by Super Admin."
            );
        }
        UUID orgId = roleDto.getOrganisationId();

        Role role = new Role();
        role.setName(roleName);
        role.setDescription(roleDto.getDescription());

        role.setPermissions(new HashSet<>());

        Organisation organisation = orgId != null
                ? organisationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Organisation not found: " + orgId))
                : null;

        role.setOrganisation(organisation);

        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findByIsDeletedFalse();
    }

    public Optional<Role> getRoleById(UUID id) {
        return roleRepository.findById(id);
    }


    public Role updateRole(UUID id, RoleDto roleDto) {
        Role role = roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found or deleted: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equals("ROLE_Super Admin"));

        /* ---------- NAME ---------- */
        if (roleDto.getName() != null && !roleDto.getName().isBlank()) {
            String newName = roleDto.getName().trim();
            String normalizedNewName = newName.toUpperCase();

            if (!isSuperAdmin && RESERVED_ROLE_NAMES.contains(normalizedNewName)) {
                throw new IllegalArgumentException(
                        "Role name '" + newName + "' is reserved and can only be managed by Super Admin."
                );
            }

            role.setName(newName);
        }

        /* ---------- DESCRIPTION ---------- */
        if (roleDto.getDescription() != null) {
            role.setDescription(roleDto.getDescription());
        }

        /* ---------- PERMISSIONS ---------- */
        if (roleDto.getPermissions() != null) {
            Set<Permission> permissionSet = roleDto.getPermissions().isEmpty()
                    ? new HashSet<>()
                    : roleDto.getPermissions().stream()
                    .map(permissionStr -> {
                        try {
                            return Permission.valueOf(permissionStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid permission: " + permissionStr);
                        }
                    })
                    .collect(Collectors.toSet());

            role.setPermissions(permissionSet);
        }

        /* ---------- ORGANISATION (PATCH-SAFE) ---------- */
        if (roleDto.getOrganisationId() != null) {
            Organisation organisation = organisationRepository
                    .findById(roleDto.getOrganisationId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Organisation not found: " + roleDto.getOrganisationId()));

            role.setOrganisation(organisation);
        }
        // IMPORTANT:
        // If organisationId is NOT sent → organisation remains unchanged

        return roleRepository.save(role);
    }



    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        role.setIsDeleted(true);
        roleRepository.save(role);
    }

    public List<Role> getRolesByOrganisationId(UUID orgId) {
        if (orgId == null) {
            throw new IllegalArgumentException("Organisation ID cannot be null");
        }
        return roleRepository.findByOrganisationIdAndIsDeletedFalse(orgId);
    }
}
