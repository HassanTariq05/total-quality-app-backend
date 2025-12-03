package com.i2p.accreditations.model.checklist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.i2p.accreditations.model.accreditation.Accreditation;
import com.i2p.accreditations.model.chapter.Chapter;
import com.i2p.accreditations.model.checklistFormat.ChecklistFormat;
import com.i2p.accreditations.model.checklistSubmission.ChecklistSubmission;
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
@Table(name = "checklists")
@JsonIgnoreProperties({"checklistFormats", "submissions"})
public class Checklist {

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
    @JsonIgnoreProperties({"checklists"})
    private Chapter chapter;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"checklist"})
    private java.util.List<ChecklistFormat> checklistFormats = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"checklist"})
    private java.util.List<ChecklistSubmission> submissions = new java.util.ArrayList<>();


    public Checklist() {}

    public Checklist(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }
}

