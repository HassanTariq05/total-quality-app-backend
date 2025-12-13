package com.i2p.accreditations.service.policyVersion;

import com.i2p.accreditations.dto.PolicyVersionCreateDto;
import com.i2p.accreditations.dto.PolicyVersionDto;
import com.i2p.accreditations.enums.PolicyVersionStatus;
import com.i2p.accreditations.model.policy.Policy;
import com.i2p.accreditations.model.policyVersion.PolicyVersion;
import com.i2p.accreditations.repository.policy.PolicyRepository;
import com.i2p.accreditations.repository.policyVersion.PolicyVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PolicyVersionService {

    @Autowired
    private PolicyVersionRepository versionRepository;

    @Autowired
    private PolicyRepository policyRepository;


    @Transactional
    public PolicyVersionDto createVersion(UUID policyId, PolicyVersionCreateDto dto) throws IOException {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        Long nextVersionNumber = versionRepository.findTopByPolicyIdOrderByNumberDesc(policyId)
                .map(v -> v.getNumber() + 1)
                .orElse(1L);

        byte[] documentBytes = null;
        String documentName = null;

        PolicyVersion latestVersion = versionRepository.findTopByPolicyIdOrderByNumberDesc(policyId).orElse(null);

        if (latestVersion != null && latestVersion.getDocument() != null) {
            documentBytes = latestVersion.getDocument().clone();
            documentName = latestVersion.getDocumentName();
        } else if (policy.getDocument() != null) {
            documentBytes = policy.getDocument().clone();
            documentName = policy.getDocumentName();
        }

        if (documentBytes == null && (dto.getDocument() == null || dto.getDocument().isEmpty())) {
            throw new IllegalArgumentException("No document available to base the new version on");
        }

        PolicyVersion newVersion = new PolicyVersion();
        newVersion.setNumber(nextVersionNumber);
        newVersion.setPolicy(policy);
        newVersion.setStatus(PolicyVersionStatus.DRAFT);

        newVersion.setTitle(dto.getTitle() != null ? dto.getTitle() : policy.getTitle());
        newVersion.setDescription(dto.getDescription());

        if (dto.getDocument() != null && !dto.getDocument().isEmpty()) {
            newVersion.setDocument(dto.getDocument().getBytes());
            newVersion.setDocumentName(dto.getDocument().getOriginalFilename());
        } else {
            newVersion.setDocument(documentBytes);
            newVersion.setDocumentName(documentName);
        }

        newVersion = versionRepository.save(newVersion);
        return convertToDto(newVersion);
    }

    @Transactional
    public PolicyVersionDto cloneLastestVersion(UUID policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        PolicyVersion latestVersion = versionRepository
                .findTopByPolicyIdOrderByNumberDesc(policyId)
                .orElseThrow(() -> new RuntimeException("No versions found to clone"));

        Long nextVersionNumber = latestVersion.getNumber() + 1;

        PolicyVersion clonedVersion = new PolicyVersion();
        clonedVersion.setPolicy(policy);
        clonedVersion.setNumber(nextVersionNumber);

        clonedVersion.setStatus(PolicyVersionStatus.DRAFT);
        clonedVersion.setTitle(latestVersion.getTitle());
        clonedVersion.setDescription(latestVersion.getDescription());

        if (latestVersion.getDocument() != null) {
            clonedVersion.setDocument(latestVersion.getDocument().clone());
            clonedVersion.setDocumentName(latestVersion.getDocumentName());
        }

        PolicyVersion saved = versionRepository.save(clonedVersion);

        return convertToDto(saved);
    }


    @Transactional
    public PolicyVersionDto approveVersion(UUID versionId) {
        PolicyVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        if (version.getStatus() != PolicyVersionStatus.SENT_FOR_APPROVAL &&
                version.getStatus() != PolicyVersionStatus.SENT_FOR_REVISION) {
            throw new IllegalStateException("Only versions in 'Sent for Approval' or 'Sent for Revision' can be approved");
        }

        Policy policy = version.getPolicy();

        versionRepository.findByPolicyIdAndStatus(policy.getId(), PolicyVersionStatus.APPROVED)
                .ifPresent(prev -> {
                    prev.setStatus(PolicyVersionStatus.ARCHIVED);
                    versionRepository.save(prev);
                });

        version.setStatus(PolicyVersionStatus.APPROVED);
        versionRepository.save(version);

        policy.setTitle(version.getTitle());
        policy.setDescription(version.getDescription());
        policy.setDocument(version.getDocument().clone());
        policy.setDocumentName(version.getDocumentName());
        policy.setStatus("ACTIVE");
        policyRepository.save(policy);

        return convertToDto(version);
    }

    @Transactional
    public PolicyVersionDto sendForApproval(UUID versionId) {
        return updateStatus(versionId, PolicyVersionStatus.SENT_FOR_APPROVAL);
    }

    @Transactional
    public PolicyVersionDto reject(UUID versionId) {
        return updateStatus(versionId, PolicyVersionStatus.REJECTED);
    }

    @Transactional
    public PolicyVersionDto sendForRevision(UUID versionId) {
        return updateStatus(versionId, PolicyVersionStatus.SENT_FOR_REVISION);
    }

    private PolicyVersionDto updateStatus(UUID versionId, PolicyVersionStatus newStatus) {
        PolicyVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        if (version.getStatus() == PolicyVersionStatus.APPROVED ||
                version.getStatus() == PolicyVersionStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot change status of approved or archived version");
        }

        version.setStatus(newStatus);
        versionRepository.save(version);
        return convertToDto(version);
    }

    public List<PolicyVersionDto> getVersionsByPolicyId(UUID policyId) {
        return versionRepository.findByPolicyId(policyId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public PolicyVersion getVersionById(UUID versionId) {
        return versionRepository.findById(versionId).orElse(null);
    }

    public PolicyVersion getVersionEntity(UUID versionId) {
        return versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
    }

    public PolicyVersionDto getLatestVersion(UUID policyId) {
        return versionRepository.findTopByPolicyIdOrderByNumberDesc(policyId)
                .map(this::convertToDto)
                .orElse(null);
    }

    public PolicyVersionDto getVersionByNumber(UUID policyId, Long versionNumber) {
        return versionRepository.findByPolicyIdAndNumber(policyId, versionNumber)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional
    public PolicyVersionDto updateVersion(UUID versionId, PolicyVersionCreateDto dto) throws IOException {
        PolicyVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        if (version.getStatus() == PolicyVersionStatus.APPROVED ||
                version.getStatus() == PolicyVersionStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot update an APPROVED or ARCHIVED version");
        }

        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            version.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            version.setDescription(dto.getDescription());
        }

        if (dto.getDocument() != null && !dto.getDocument().isEmpty()) {
            version.setDocument(dto.getDocument().getBytes());
            version.setDocumentName(dto.getDocument().getOriginalFilename());
        }

        if (dto.getStatus() != null) {
            version.setStatus(dto.getStatus());
        }

        version = versionRepository.save(version);
        return convertToDto(version);
    }
    @Transactional
    public void deleteVersion(UUID versionId) {
        PolicyVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));

        versionRepository.delete(version);
    }

    private PolicyVersionDto convertToDto(PolicyVersion version) {
        PolicyVersionDto dto = new PolicyVersionDto();
        dto.setId(version.getId());
        dto.setNumber(version.getNumber());
        dto.setTitle(version.getTitle());
        dto.setDescription(version.getDescription());
        dto.setStatus(version.getStatus().name());
        dto.setPolicyId(version.getPolicy().getId());
        dto.setDocumentName(version.getDocumentName());
        return dto;
    }
}