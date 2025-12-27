package com.i2p.accreditations.controller.policy;

import com.i2p.accreditations.model.policy.Policy;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.policy.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@ProtectedEndpoint
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService service;

    @Autowired
    public PolicyController(PolicyService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('PERMISSION_CREATE_POLICY')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Policy> create(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("status") String status,
            @RequestParam("chapterId") UUID chapterId,
            @RequestParam(value = "document", required = false) MultipartFile document
    ) throws IOException {
        return ResponseEntity.ok(service.createPolicy(title, description, status, chapterId, document));
    }



    @PreAuthorize("hasAuthority('PERMISSION_VIEW_POLICY')")
    @GetMapping("/getAllByChapterId/{id}")
    public ResponseEntity<Page<Policy>> getAllByChapterId(
            @PathVariable("id") UUID chapterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAllPolicys(chapterId, pageable));
    }

    @PreAuthorize("hasAuthority('PERMISSION_VIEW_POLICY')")
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getById(@PathVariable UUID id) {
        return service.getPolicyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('PERMISSION_EDIT_POLICY')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Policy> update(
            @PathVariable UUID id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("status") String status,
            @RequestParam(value = "chapterId", required = false) UUID chapterId,
            @RequestParam(value = "document", required = false) MultipartFile document
    ) throws IOException {
        return ResponseEntity.ok(service.updatePolicy(id, title, description, status, chapterId, document));
    }


    @PreAuthorize("hasAuthority('PERMISSION_DELETE_POLICY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}