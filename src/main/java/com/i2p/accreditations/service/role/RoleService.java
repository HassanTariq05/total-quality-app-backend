package com.i2p.accreditations.service.role;

import com.i2p.accreditations.dto.RoleDto;
import com.i2p.accreditations.enums.Permission;
import com.i2p.accreditations.model.role.Role;
import com.i2p.accreditations.repository.role.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(RoleDto roleDto) {
        if (roleRepository.existsByNameAndIsDeletedFalse(roleDto.getName())) {
            throw new IllegalArgumentException(
                    "Role already exists: " + roleDto.getName()
            );
        }
        Role role = new Role();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        role.setPermissions(new HashSet<>());
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findByIsDeletedFalse();
    }

    public Optional<Role> getRoleById(UUID id) {
        return roleRepository.findById(id);
    }

    public Role updateRole(UUID id, RoleDto roleDto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        if (roleDto.getName() != null && !roleDto.getName().isBlank()) {
            role.setName(roleDto.getName());
        }

        if (roleDto.getDescription() != null) {
            role.setDescription(roleDto.getDescription());
        }

        if (roleDto.getPermissions() != null && !roleDto.getPermissions().isEmpty()) {
            Set<Permission> permissionSet = roleDto.getPermissions()
                    .stream()
                    .map(permissionStr -> {
                        try {
                            return Permission.valueOf(permissionStr);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid permission: " + permissionStr);
                        }
                    })
                    .collect(Collectors.toSet());

            role.setPermissions(permissionSet);
        }

        return roleRepository.save(role);
    }


    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        role.setIsDeleted(true);
        roleRepository.save(role);
    }
}
