package com.i2p.accreditations.model.checklistFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.i2p.accreditations.model.checklist.Checklist;
import com.i2p.accreditations.model.form.Form;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "checklist_formats")
public class ChecklistFormat {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Long number;

    @Lob
    @Column(name = "format", columnDefinition = "TEXT")
    private String format;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "checklist_id", referencedColumnName = "id")
//    @JsonIgnoreProperties({"checklist_format"})
//    private Checklist checklist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checklist_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"checklistFormats", "submissions"})
    private Checklist checklist;

    public ChecklistFormat() {}

    public ChecklistFormat(String format) {
        this.format = format;
    }
}

