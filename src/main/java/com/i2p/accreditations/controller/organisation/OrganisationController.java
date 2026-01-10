package com.i2p.accreditations.controller.organisation;

import com.i2p.accreditations.dto.OrganisationRequestDto;
import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.organisation.Organisation;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.organisation.OrganisationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/organizations")
public class OrganisationController {

    private final OrganisationService service;

    public OrganisationController(OrganisationService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('Super Admin')")
    public ResponseEntity<Organisation> create(@RequestBody OrganisationRequestDto organisation) {
        return ResponseEntity.ok(service.createOrganisation(organisation));
    }

    @GetMapping("/{orgId}/accreditations")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW_ACCREDITATION')")
    public ResponseEntity<List<Accreditation>> getAccreditationsByOrganization(
            @PathVariable UUID orgId) {

        List<Accreditation> accreditations = service.getAccreditationsByOrganisationIdEfficient(orgId);

        return ResponseEntity.ok(accreditations);
    }

    @GetMapping
    @PreAuthorize("hasRole('Super Admin')")
    public ResponseEntity<List<Organisation>> getAll() {
        return ResponseEntity.ok(service.getAllOrganisations());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Super Admin','Administrator')")
    public ResponseEntity<Organisation> getById(@PathVariable UUID id) {
        return service.getOrganisationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Super Admin','Administrator')")
    public ResponseEntity<Organisation> update(
            @PathVariable UUID id,
            @RequestBody OrganisationRequestDto dto) {
        return ResponseEntity.ok(service.updateOrganisation(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('Super Admin','Administrator')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteOrganisation(id);
        return ResponseEntity.noContent().build();
    }
}
