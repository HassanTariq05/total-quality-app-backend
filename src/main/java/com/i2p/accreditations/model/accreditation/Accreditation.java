package com.i2p.accreditations.model.accreditation;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.i2p.accreditations.model.chapter.Chapter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name = "accreditations")
public class Accreditation {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String status;

    public Accreditation() {}

    public Accreditation(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }
}
