package com.i2p.accreditations.controller.policyVersion;

import com.i2p.accreditations.dto.PolicyVersionCreateDto;
import com.i2p.accreditations.dto.PolicyVersionDto;
import com.i2p.accreditations.model.policyVersion.PolicyVersion;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.policyVersion.PolicyVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@ProtectedEndpoint
@RequestMapping("/api/policyVersions")
public class PolicyVersionController {

    @Autowired
    private PolicyVersionService versionService;

    @GetMapping
    public ResponseEntity<List<PolicyVersionDto>> getAllVersions(@RequestParam UUID policyId) {
        return ResponseEntity.ok(versionService.getVersionsByPolicyId(policyId));
    }

    @GetMapping("/{policyVersionId}")
    public ResponseEntity<PolicyVersion> getVersionById(@PathVariable UUID policyVersionId) {
        PolicyVersion version = versionService.getVersionById(policyVersionId);
        return version != null
                ? ResponseEntity.ok(version)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/latest")
    public ResponseEntity<PolicyVersionDto> getLatestVersion(@RequestParam UUID policyId) {
        PolicyVersionDto version = versionService.getLatestVersion(policyId);
        return version != null ? ResponseEntity.ok(version) : ResponseEntity.notFound().build();
    }

    @GetMapping("/number/{versionNumber}")
    public ResponseEntity<PolicyVersionDto> getVersionByNumber(
            @RequestParam UUID policyId,
            @PathVariable Long versionNumber) {
        PolicyVersionDto version = versionService.getVersionByNumber(policyId, versionNumber);
        return version != null ? ResponseEntity.ok(version) : ResponseEntity.notFound().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PolicyVersionDto> createVersion(
            @RequestParam UUID policyId,
            @ModelAttribute PolicyVersionCreateDto dto) {
        try {
            PolicyVersionDto created = versionService.createVersion(policyId, dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value = "/cloneLastest")
    public ResponseEntity<PolicyVersionDto> cloneLatestVersion(@RequestParam UUID policyId) {
        try {
            PolicyVersionDto cloned = versionService.cloneLastestVersion(policyId);
            return ResponseEntity.ok(cloned);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(value = "/{versionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PolicyVersionDto> updateVersion(
            @PathVariable UUID versionId,
            @ModelAttribute PolicyVersionCreateDto dto) {
        try {
            PolicyVersionDto updated = versionService.updateVersion(versionId, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{versionId}/document")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable UUID versionId) {
        PolicyVersion version = versionService.getVersionEntity(versionId);
        if (version.getDocument() == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(version.getDocument());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + version.getDocumentName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(version.getDocument().length)
                .body(resource);
    }

    @DeleteMapping("/{versionId}")
    public ResponseEntity<Void> deleteVersion(@PathVariable UUID versionId) {
        try {
            versionService.deleteVersion(versionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{versionId}/send-for-approval")
    public ResponseEntity<PolicyVersionDto> sendForApproval(
            @PathVariable UUID versionId) {
        return ResponseEntity.ok(versionService.sendForApproval(versionId));
    }

    @PostMapping("/{versionId}/approve")
    public ResponseEntity<PolicyVersionDto> approveVersion(@PathVariable UUID versionId) {
        return ResponseEntity.ok(versionService.approveVersion(versionId));
    }

    @PostMapping("/{versionId}/reject")
    public ResponseEntity<PolicyVersionDto> reject(@PathVariable UUID versionId) {
        return ResponseEntity.ok(versionService.reject(versionId));
    }

    @PostMapping("/{versionId}/send-for-revision")
    public ResponseEntity<PolicyVersionDto> sendForRevision(@PathVariable UUID versionId) {
        return ResponseEntity.ok(versionService.sendForRevision(versionId));
    }
}