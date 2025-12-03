package com.i2p.accreditations.dto;

import com.i2p.accreditations.model.form.Form;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PolicyDto {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private UUID chapterId;

    public PolicyDto(Form c) {
        this.id = c.getId();
        this.chapterId = (c.getChapter() != null) ? c.getChapter().getId() : null;
        this.title = c.getTitle();
        this.description = c.getDescription();
        this.status = c.getStatus();
    }
}
