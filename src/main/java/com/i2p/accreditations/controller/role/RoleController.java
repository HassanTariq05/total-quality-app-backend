package com.i2p.accreditations.controller.role;

import com.i2p.accreditations.dto.RoleDto;
import com.i2p.accreditations.model.role.Role;
import com.i2p.accreditations.service.role.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('Super Admin', 'Administrator')")
    public ResponseEntity<Role> createRole(@RequestBody RoleDto roleDto) {
        Role role = roleService.createRole(roleDto);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    @PreAuthorize("hasRole('Super Admin')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }


    @GetMapping("/org/{orgId}")
    @PreAuthorize("hasAnyRole('Super Admin', 'Administrator')")
    public ResponseEntity<List<Role>> getRolesByOrgId(@PathVariable UUID orgId) {
        return ResponseEntity.ok(roleService.getRolesByOrganisationId(orgId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Super Admin', 'Administrator')")
    public ResponseEntity<Role> getRoleById(@PathVariable UUID id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Super Admin', 'Administrator')")
    public ResponseEntity<Role> updateRole(
            @PathVariable UUID id,
            @RequestBody RoleDto roleDto) {

        Role updatedRole = roleService.updateRole(id, roleDto);
        return ResponseEntity.ok(updatedRole);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Super Admin', 'Administrator')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
