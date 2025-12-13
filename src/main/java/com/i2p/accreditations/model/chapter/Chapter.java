package com.i2p.accreditations.model.chapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.policy.Policy;
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
@Table(name = "chapters")
public class Chapter {

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
    @JoinColumn(name = "accreditation_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"chapters"})
    private Accreditation accreditation;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Form> forms = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Checklist> checklists = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Policy> policies = new ArrayList<>();


    public Chapter() {}

    public Chapter(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }
}

