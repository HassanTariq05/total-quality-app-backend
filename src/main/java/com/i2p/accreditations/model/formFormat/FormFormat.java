package com.i2p.accreditations.model.formFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "form_formats")
public class FormFormat {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Long number;

    @Lob
    @Column(name = "format", columnDefinition = "TEXT")
    private String format;

    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"form_format"})
    private Form form;*/

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"formFormats", "submissions"})
    private Form form;

    public FormFormat() {}

    public FormFormat(String format) {
        this.format = format;
    }
}

