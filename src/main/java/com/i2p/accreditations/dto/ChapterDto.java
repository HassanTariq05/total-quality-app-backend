package com.i2p.accreditations.dto;

import com.i2p.accreditations.model.chapter.Chapter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ChapterDto {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private UUID accreditationId;

    public ChapterDto(Chapter c) {
        this.id = c.getId();
        this.accreditationId = (c.getAccreditation() != null) ? c.getAccreditation().getId() : null;
        this.title = c.getTitle();
        this.description = c.getDescription();
        this.status = c.getStatus();
    }
}

