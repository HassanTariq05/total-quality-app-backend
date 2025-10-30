package com.i2p.accreditations.dto;

import com.i2p.accreditations.model.form.Form;
import com.i2p.accreditations.model.formFormat.FormFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
public class FormFormatDto {
    private UUID id;
    private String format;
    private UUID formId;

    public FormFormatDto(FormFormat c) {
        this.id = c.getId();
        this.formId = (c.getForm() != null) ? c.getForm().getId() : null;
        this.format  = c.getFormat();
    }
}

