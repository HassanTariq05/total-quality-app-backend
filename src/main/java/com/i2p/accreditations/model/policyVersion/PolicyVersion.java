package com.i2p.accreditations.model.policyVersion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.i2p.accreditations.enums.PolicyVersionStatus;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.model.policy.Policy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "policy_versions")
public class PolicyVersion {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private Long number;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private PolicyVersionStatus status = PolicyVersionStatus.DRAFT;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"policy_versions"})
    private Policy policy;

    @Lob
    @Column(name = "document")
    private byte[] document;

    @Column(name = "document_name")
    private String documentName;

    public PolicyVersion() {}
}


