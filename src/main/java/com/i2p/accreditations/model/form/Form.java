package com.i2p.accreditations.model.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.model.formFormat.FormFormat;
import com.i2p.accreditations.model.formSubmission.FormSubmission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "forms")
@JsonIgnoreProperties({"formFormats", "submissions"})
public class Form {

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
    @JsonIgnoreProperties({"forms"})
    private Chapter chapter;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"form"})
    private java.util.List<FormFormat> formFormats = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"form"})
    private java.util.List<FormSubmission> submissions = new java.util.ArrayList<>();

    public Form() {}

    public Form(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }
}


