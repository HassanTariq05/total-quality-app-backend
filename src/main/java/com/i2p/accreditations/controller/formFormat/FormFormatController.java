package com.i2p.accreditations.controller.formFormat;

import com.i2p.accreditations.dto.FormFormatDto;
import com.i2p.accreditations.model.formFormat.FormFormat;
import com.i2p.accreditations.security.annotations.ProtectedEndpoint;
import com.i2p.accreditations.service.formFormat.FormFormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@ProtectedEndpoint
@RequestMapping("/api/formFormats")
public class FormFormatController {

    private final FormFormatService service;

    @Autowired
    public FormFormatController(FormFormatService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('PERMISSION_CREATE_FORM')")
    @PostMapping
    public ResponseEntity<FormFormat> create(@RequestBody FormFormatDto formDto) {
        return ResponseEntity.ok(service.createFormFormat(formDto));
    }


    @PreAuthorize("hasAuthority('PERMISSION_VIEW_FORM')")
    @GetMapping("/getFormFormatByFormId/{id}")
    public ResponseEntity<FormFormat> getFormFormatByFormId(@PathVariable("id") UUID formId) {
        FormFormat formFormat = service.getFormFormatByFormId(formId);
        return ResponseEntity.ok(formFormat);
    }

    @PreAuthorize("hasAuthority('PERMISSION_EDIT_FORM')")
    @PutMapping("/{id}")
    public ResponseEntity<FormFormat> update(@PathVariable UUID id, @RequestBody FormFormat formFormat) {
        return ResponseEntity.ok(service.updateFormFormat(id, formFormat));
    }

    @PreAuthorize("hasAuthority('PERMISSION_DELETE_FORM')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteFormFormat(id);
        return ResponseEntity.noContent().build();
    }
}

