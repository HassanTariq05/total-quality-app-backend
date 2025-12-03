package com.i2p.accreditations.model.formSubmission;

import com.i2p.accreditations.model.access.User;
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
@Table(name = "form_submissions")
public class FormSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User submittedBy;

    @Lob
    private String data;

    private LocalDateTime submittedAt;
}
