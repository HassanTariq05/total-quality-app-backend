package com.i2p.accreditations.controller.accreditation;

import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.accreditation.AccreditationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accreditations")
@ProtectedEndpoint
public class AccreditationController {

    private final AccreditationService service;

    public AccreditationController(AccreditationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Accreditation> create(@RequestBody Accreditation accreditation) {
        return ResponseEntity.ok(service.createAccreditation(accreditation));
    }

    @GetMapping
    public ResponseEntity<List<Accreditation>> getAll() {
        return ResponseEntity.ok(service.getAllAccreditations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Accreditation> getById(@PathVariable UUID id) {
        return service.getAccreditationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Accreditation> update(@PathVariable UUID id, @RequestBody Accreditation accreditation) {
        return ResponseEntity.ok(service.updateAccreditation(id, accreditation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteAccreditation(id);
        return ResponseEntity.noContent().build();
    }
}
