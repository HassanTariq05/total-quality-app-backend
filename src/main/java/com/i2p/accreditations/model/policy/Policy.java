package com.i2p.accreditations.model.policy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.model.policyVersion.PolicyVersion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Long number;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chapter_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"policies"})
    private Chapter chapter;

    @Lob
    @Column(name = "document")
    private byte[] document;

    @Column(name = "document_name")
    private String documentName;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PolicyVersion> policyVersions = new ArrayList<>();

    public Policy() {}

    public Policy(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }
}


