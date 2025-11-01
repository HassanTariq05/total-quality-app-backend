package com.i2p.accreditations.dto;

import com.i2p.accreditations.model.checklistFormat.ChecklistFormat;
import com.i2p.accreditations.model.formFormat.FormFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ChecklistFormatDto {
    private UUID id;
    private String format;
    private UUID checklistId;

    public ChecklistFormatDto(ChecklistFormat c) {
        this.id = c.getId();
        this.checklistId = (c.getChecklist() != null) ? c.getChecklist().getId() : null;
        this.format  = c.getFormat();
    }
}
