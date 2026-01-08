package com.i2p.accreditations.model.checklistSubmission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.i2p.accreditations.model.access.User;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.organisation.Organisation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "checklist_submissions")
public class ChecklistSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    @JsonIgnore
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User submittedBy;

    @Lob
    private String data;

    private LocalDateTime submittedAt;
}
