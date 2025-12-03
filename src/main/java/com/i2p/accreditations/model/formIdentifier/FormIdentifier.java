package com.i2p.accreditations.model.formIdentifier;

import com.i2p.accreditations.model.formSubmission.FormSubmission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "form_identifiers")
public class FormIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private FormSubmission submission;
}
