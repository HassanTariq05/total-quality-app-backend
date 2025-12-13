package com.i2p.accreditations.repository.policyVersion;

import com.i2p.accreditations.enums.PolicyVersionStatus;
import com.i2p.accreditations.model.policyVersion.PolicyVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PolicyVersionRepository extends JpaRepository<PolicyVersion, UUID> {

    List<PolicyVersion> findByPolicyId(UUID policyId);

    Optional<PolicyVersion> findByPolicyIdAndNumber(UUID policyId, Long versionNumber);

    Optional<PolicyVersion> findTopByPolicyIdOrderByNumberDesc(UUID policyId);

    boolean existsByNumber(Long number);

    Optional<PolicyVersion> findByPolicyIdAndStatus(UUID policyId, PolicyVersionStatus status);

    List<PolicyVersion> findByPolicyIdAndStatusIn(UUID policyId, List<PolicyVersionStatus> statuses);

    Optional<PolicyVersion> findTopByPolicyIdAndStatusInOrderByNumberDesc(
            UUID policyId, List<PolicyVersionStatus> statuses);

    List<PolicyVersion> findByPolicyIdOrderByNumberDesc(UUID policyId);
}