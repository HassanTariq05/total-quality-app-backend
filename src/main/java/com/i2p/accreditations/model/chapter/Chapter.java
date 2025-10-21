package com.i2p.accreditations.model.chapter;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.i2p.accreditations.model.accreditation.Accreditation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "accreditation_id", referencedColumnName = "id")
    private Accreditation accreditation;

    public Chapter() {}

    public Chapter(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }
}

