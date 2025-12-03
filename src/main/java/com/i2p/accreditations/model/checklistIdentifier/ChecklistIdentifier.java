package com.i2p.accreditations.model.checklistIdentifier;

import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "checklist_identifiers")
public class ChecklistIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private ChecklistSubmission submission;
}